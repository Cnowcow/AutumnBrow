package autumn.browmanagement.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class FtpUtil {
    private static final String FTP_SERVER = "hhjnn92.synology.me"; // FTP 서버 주소
    private static final int FTP_PORT = 2200; // FTP 포트 번호
    private static final String FTP_USER = "anonymous";    // FTP 사용자명
    private static final String FTP_PASS = "";    // FTP 비밀번호

    private FTPClient ftpClient;

    public FtpUtil() {
        ftpClient = new FTPClient();
    }

    // FTP 서버에 연결
    public void connect() throws IOException {
        ftpClient.connect(FTP_SERVER, FTP_PORT); // 서버 주소와 포트
        boolean success = ftpClient.login(FTP_USER, FTP_PASS);
        if (!success) {
            throw new IOException("FTP 서버 로그인 실패");
        }
        ftpClient.enterLocalPassiveMode();  // Passive 모드 사용
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);  // 바이너리 파일 타입 설정
    }

    // 파일 업로드
    public boolean uploadFile(String remoteFilePath, File localFile) throws IOException {
        try (InputStream inputStream = new FileInputStream(localFile)) {
            boolean done = ftpClient.storeFile(remoteFilePath, inputStream);
            if (!done) {
                throw new IOException("파일 업로드 실패");
            }
            return true;
        }
    }

    // 파일 다운로드
    public void downloadFile(String remoteFilePath, String localFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
            boolean success = ftpClient.retrieveFile(remoteFilePath, fos);
            if (!success) {
                throw new IOException("파일 다운로드 실패");
            }
        }
    }

    // 파일 삭제
    public boolean deleteFile(String remoteFilePath) {
        try {
            // FTP 서버에서 파일 삭제 로직
            ftpClient.deleteFile(remoteFilePath);
            return true; // 삭제 성공 시 true 반환
        } catch (IOException e) {
            e.printStackTrace();
            return false; // 삭제 실패 시 false 반환
        }
    }


    // FTP 연결 종료
    public void disconnect() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}