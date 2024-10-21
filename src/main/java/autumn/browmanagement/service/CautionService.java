package autumn.browmanagement.service;

import autumn.browmanagement.DTO.CautionDTO;
import autumn.browmanagement.config.FtpUtil;
import autumn.browmanagement.domain.Caution;
import autumn.browmanagement.repository.CautionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CautionService {

    private final CautionRepository cautionRepository;
    private final FtpUtil ftpUtil;


    public void handleFileUpload(CautionDTO cautionDTO) throws IOException {
        ftpUtil.connect(); // FTP 연결

        try {
            uploadFile(cautionDTO.getBeforeImage(), cautionDTO, true);
            uploadFile(cautionDTO.getAfterImage(), cautionDTO, false);
        } finally {
            ftpUtil.disconnect(); // FTP 연결 종료
        }
    }

    private void uploadFile(MultipartFile file, CautionDTO cautionDTO, boolean isBefore) throws IOException {
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            File localFile = new File(System.getProperty("java.io.tmpdir") + "/" + uniqueFileName);
            file.transferTo(localFile);
            ftpUtil.uploadFile("/autumnBrow/caution/" + uniqueFileName, localFile);

            if (isBefore) {
                cautionDTO.setBeforeUrl(uniqueFileName); // 비포 URL 설정
            } else {
                cautionDTO.setAfterUrl(uniqueFileName); // 애프터 URL 설정
            }
            localFile.delete(); // 임시 파일 삭제
        }
    }

    @Transactional
    public Caution createCatuion(CautionDTO cautionDTO){
        Caution caution = new Caution();
        caution.setBeforeTitle(cautionDTO.getBeforeTitle());
        caution.setBeforeUrl(cautionDTO.getBeforeUrl());
        caution.setBeforeText(cautionDTO.getBeforeText());
        caution.setAfterTitle(cautionDTO.getAfterTitle());
        caution.setAfterUrl(cautionDTO.getAfterUrl());

        cautionRepository.save(caution);

        return caution;
    }


    // 시술 전 주의사항
    @Transactional
    public List<CautionDTO> cautionFindBefore(){
        List<Caution> cautions = cautionRepository.findAllOrderByAfterTitleIsNull();
        List<CautionDTO> cautionDTOs = new ArrayList<>();

        for (Caution caution : cautions){
            CautionDTO cautionDTO = new CautionDTO();
            cautionDTO.setCautionId(caution.getCautionId());
            cautionDTO.setBeforeTitle(caution.getBeforeTitle());
            cautionDTO.setBeforeUrl(caution.getBeforeUrl());
            cautionDTO.setBeforeText(caution.getBeforeText());
            cautionDTO.setAfterTitle("null");

            cautionDTOs.add(cautionDTO);
        }
        return cautionDTOs;
    }


    // 시술 후 주의사항
    @Transactional
    public List<CautionDTO> cautionFindAfter(){
        List<Caution> cautions = cautionRepository.findAllOrderByBeforeTitleIsNull();
        List<CautionDTO> cautionDTOs = new ArrayList<>();

        for (Caution caution : cautions){
            CautionDTO cautionDTO = new CautionDTO();
            cautionDTO.setCautionId(caution.getCautionId());
            cautionDTO.setAfterTitle(caution.getAfterTitle());
            cautionDTO.setAfterUrl(caution.getAfterUrl());
            cautionDTO.setAfterText(caution.getAfterText());
            cautionDTO.setBeforeTitle("null");

            cautionDTOs.add(cautionDTO);
        }
        return cautionDTOs;
    }
}
