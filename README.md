Readme 작성중입니다....

# AutumnBrow <sup>(2024.10.10 - 진행중)</sup>

<p align="center">
  <img alt="image" src="https://github.com/user-attachments/assets/e89940be-164a-418b-ab23-e3a28e398589" />
</p>

# 💌 소개
### AutumnBrow 뷰티샵 고객관리 페이지
- 회원관리
- 시술내역 관리
- 시술 예약기능
- 공지사항 / 이벤트 / 주의사항 / 후기 등 커뮤니티
<br><br>

# 📚 배포 페이지
### 🤨 [AutumnBrow](http://autumnbrow.hhjnn92.synology.me/)
<br><br>

# 🛠️ 기술 스택
### Java / Spring Boot / Spring MVC / RESTful API / Mysql / Spring Data JPA <br>
### Lombok / Tomcat / FTP/  Git / Docker / KakaoMap API / Thymeleaf 
<br><br>

# 🔀 아키텍쳐
<br><br>

# ⛓️‍💥 ERD
![ERD](https://github.com/user-attachments/assets/9de29eff-d6a6-40ba-9b70-2589c6fb416f)
<br><br>

# 💻 구현 기능
- ### 프로젝트 환경 / 구조 설정
- ### 이미지 / 데이터베이스 서버 구축
  <details>
    <summary> Synology FTP서버에 이미지 업로드 처리
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>
    
    <h2>FTP 연결, 업로드, 연결해제 컴포넌트 클래스 작성 후, 필요한 service에서 사용.</h2>
  
    ```java
      @Component
      public class FtpUtil {
      private static final String FTP_SERVER = // FTP 서버 주소;
      private static final int FTP_PORT = // FTP 포트 번호;
      private static final String FTP_USER = // FTP 사용자명;
      private static final String FTP_PASS = // FTP 비밀번호;
  
      private FTPClient ftpClient;
  
      public FtpUtil() {
          ftpClient = new FTPClient();
      }
  
      // FTP 서버에 연결
      public void connect() throws IOException {
          ftpClient.connect(FTP_SERVER, FTP_PORT);
          boolean success = ftpClient.login(FTP_USER, FTP_PASS);
          if (!success) {
              throw new IOException("FTP 서버 로그인 실패");
          }
          ftpClient.enterLocalPassiveMode();
          ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
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
    ```
    <br>
  
    <h2>UUID를 사용해서 파일명 중복처리 후, service에 따라 폴더명을 다르게 해서 이미지파일 업로드 후 관리.</h2>
    
    ```java
      public void handleFileUpload(PostDTO postDTO) throws IOException {
          ftpUtil.connect(); // FTP 연결
  
          try {
              uploadFile(postDTO.getBeforeImageFile(), postDTO, true);
              uploadFile(postDTO.getAfterImageFile(), postDTO, false);
          } finally {
              ftpUtil.disconnect(); // FTP 연결 종료
          }
      }
  
      private void uploadFile(MultipartFile file, PostDTO postDTO, boolean isBefore) throws IOException {
          if (file != null && !file.isEmpty()) {
              String originalFilename = file.getOriginalFilename();
              String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
              String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
              File localFile = new File(System.getProperty("java.io.tmpdir") + "/" + uniqueFileName);
              file.transferTo(localFile);
              ftpUtil.uploadFile("/Project/AutumnBrow/BeforeAndAfter/" + uniqueFileName, localFile);
  
              if (isBefore) {
                  postDTO.setBeforeImageUrl(uniqueFileName);
              } else {
                  postDTO.setAfterImageUrl(uniqueFileName);
              }
              localFile.delete();
          }
      }
    ```

  </details>
  
  <details>
    <summary> Synology에 데이터베이스 서버 구축 (MariaDB 10)
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>
    
    **MariaDB서버를 구축했지만 Mysql과 호환 가능.**
  
    ![image](https://github.com/user-attachments/assets/06b01770-4445-44fd-8cb8-38133e6e5bf7)

    
    <br>
  
    <h2>localhost 대신 구축한 데이터베이스 서버 사용.</h2>
    
    ```java
      spring.datasource.url=jdbc:mysql://ip주소:3306/DataBase명?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
      spring.datasource.username=ID
      spring.datasource.password=PW
      spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    ```

  </details>

- ### 데이터베이스 설계 / 구현
  <details>
    <summary> ERD 작성</summary>
  </details>
    <details>
    <summary> 속성 / 관계 정의</summary>
  </details>
    <details>
    <summary> 정규화</summary>
  </details>

- ### RESTful API
- ### 회원 CRUD
  <details>
    <summary> 사용자가 직접 회원가입할 때 이름, 전화번호, 생년월일 정보만 받아서 <br>
    &nbsp;&nbsp;&nbsp;회원가입을 진행하지만전화번호를 암호화처리 해서 비밀번호로도 사용 가능
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>
    
    <h2>전화번호 암호화 후 비밀번호로 사용 (AES방식에서 Hash방식으로 전환 예정)</h2>
  
    ```java
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = "0123456789abcdef".getBytes(); // 16-byte secret key

    // 암호화 메서드
    public static String encrypt(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyValue, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedValue = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedValue);
    }
    ```
    ```java
    public void userRegister(String name, String phone, Date birthDay){
        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        try {
            user.setPassword(EncryptionUtil.encrypt(phone));
        } catch (Exception e) {
            throw new IllegalStateException("전화번호 암호화 실패", e);
        }

        user.setBirthDay(birthDay);
        user.setTreatmentCount(0L);
        user.setFirstVisitDate(new Date());
        user.setIsDeleted("N");

        // 중복 회원 검증
        userDuplicate(user);

        userRepository.save(user);
    }
    ```
    **결과**<br>
    ![암호화](https://github.com/user-attachments/assets/bc55bc4e-dfc9-47fd-b3c6-4806574aec61)
    
    ![회원가입](https://github.com/user-attachments/assets/b8c206eb-053b-486e-8a07-ea8963444f25)
  
    ![로그인](https://github.com/user-attachments/assets/bff56aa2-4817-4181-80af-c6ff095a19ed)


  </details>
  <details>
    <summary> 회원가입이 되어있지 않을 때, 관리자가 시술 내역을 등록하면 회원가입까지 같이 진행
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>
    
    <h2>입력한 정보를 사용해서 회원가입 진행, 이미 회원이라면 회원가입은 진행하지 않음</h2>

    ```java
    public void postCreate(PostDTO postDTO) throws Exception {
        Post post = new Post();

        Optional<User> findUser = userRepository.findByNameAndPhone(postDTO.getName(), postDTO.getPhone());

        User user;
        if (findUser.isPresent()) {
            user = findUser.get(); // 기존 사용자
            user.setTreatmentCount(user.getTreatmentCount() + 1L); // 방문횟수 1 증가
        } else {
            user = createUser(postDTO); // 새로운 사용자 생성
        }

    .
    .
    .
    .

    private User createUser(PostDTO postDTO) throws Exception {
        User user = new User();
        user.setName(postDTO.getName());
        user.setPhone(postDTO.getPhone());
        user.setPassword(EncryptionUtil.encrypt(postDTO.getPhone()));
        user.setBirthDay(Optional.ofNullable(postDTO.getBirthDay()).orElse(new Date()));
        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("Role ID 2 not found"));
        user.setRole(role);
        user.setTreatmentCount(1L);
        user.setFirstVisitDate(new Date());
        user.setIsDeleted("N");

        return userRepository.save(user); // 새로운 사용자 저장
    }
    ```
    
  </details>
  <details>
    <summary> Spring Security 적용 예정</summary>
  </details>

- ### 관리자용 시술내역, 공지사항, 주의사항, 이벤트 등 커뮤니티 CRUD (Readme 수정중)
  <details>
    <summary> 시술내용 등록
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>

    <h2>데이터베이스에 있는 시술내용 외에 다른 시술내용을 입력하고 싶을 경우, 직접입력 필드를 제공하여 조건에 따라 로직 처리</h2>
  
    ![등록](https://github.com/user-attachments/assets/bb05b259-bb90-48b8-a5fa-2c4f6984dddd)
  
    <br>

    시술내용(대분류), 세부내용(소뷴류) 둘 다 직접입력일 때
    ```java
    if (postDTO.getParentTreatment() == null){
      String directParentTreatment = postDTO.getDirectParentTreatment();
      String directChildTreatment = postDTO.getDirectChildTreatment();

      if (directParentTreatment != null && !directParentTreatment.isEmpty() && directChildTreatment != null && !directChildTreatment.isEmpty()) {
        // 새 대분류 생성
        parentTreatment = new Treatment();
        parentTreatment.setName(directParentTreatment);
        treatmentRepository.save(parentTreatment); // 대분류 저장

        // 새 소분류 생성
        childTreatment = new Treatment();
        childTreatment.setName(directChildTreatment);
        childTreatment.setParent(parentTreatment); // 소분류의 부모를 대분류로 설정
        treatmentRepository.save(childTreatment); // 소분류 저장
      }
    }
    ```
    
    <br>
    
    시술내용(대분류)는 기존값, 세부내용(소뷴류)는 직접입력일 때
    ```java
    else {
      parentTreatment = treatmentRepository.findById(postDTO.getParentTreatment())
              .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시술내용입니다."));
    
      if (postDTO.getChildTreatment() == null) {
          String directChildTreatment = postDTO.getDirectChildTreatment();
          if (directChildTreatment != null && !directChildTreatment.isEmpty()) {
              // 새 소분류 생성
              childTreatment = new Treatment();
              childTreatment.setName(directChildTreatment);
              childTreatment.setParent(parentTreatment); // 소분류의 부모를 대분류로 설정
              treatmentRepository.save(childTreatment); // 소분류 저장
            }
        }
    }
    ```

    <br>
    
    시술내용(대분류), 세부내용(소뷴류) 둘 다 기존 값이 있을 때
    ```java
    if (postDTO.getParentTreatment() != null && postDTO.getChildTreatment() != null) {
        Treatment existTreatment = treatmentRepository.findById(postDTO.getChildTreatment())
                  .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 소분류 ID"));
        childTreatment = existTreatment; // 기존 소분류 사용
    }
    ```

    <br>
    
    <h2>방문경로 입력 항목도 직접입력 필드 제공하여 로직 처리</h2>

    방문경로가 직접입력일 때
    ```java
    // Visit 정보 설정
    Visit visitPath = null;
      
    if (postDTO.getVisitId() == null){
      String directVisitPath = postDTO.getVisitPath();
      visitPath = new Visit();
      visitPath.setVisitPath(directVisitPath);
      visitRepository.save(visitPath);
    }
    ```
    
    방문경로가 기존값일 때
    ```java
    if (postDTO.getVisitId() != null) {
      if (postDTO.getVisitId() == 0){
          post.setVisit(null);
      }else {
          Visit visit = visitRepository.findById(postDTO.getVisitId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방문 경로입니다."));
          visitPath = visit;
      }
    }
    ```

  </details>

  <details>
    <summary> 게시물 수정 및 삭제
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>

    ![수정, 휴지통](https://github.com/user-attachments/assets/030ac5cd-08f6-4e7e-8fb1-f569347a1d5a)

    <h2>게시물 수정할 때, 필수값이 아닌 항목들을 null값 처리</h2>

    Service
    ```java
    public PostDTO postListByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

        PostDTO postDTO = new PostDTO();

        postDTO.setPostId(post.getPostId());
        postDTO.setUserId(post.getUser().getUserId()); //유저아이디
        postDTO.setName(post.getUser().getName()); // 이름
        postDTO.setPhone(post.getUser().getPhone()); // 전화번호
        postDTO.setBirthDay(post.getUser().getBirthDay()); // 생년월일
        postDTO.setFirstVisitDate(post.getUser().getFirstVisitDate()); // 첫방문 날짜
        postDTO.setTreatmentCount(post.getUser().getTreatmentCount()); // 방문횟수
        postDTO.setPostId(post.getPostId()); // 시술내용 id
        postDTO.setParentTreatment(post.getParent() != null ? post.getParent().getTreatmentId() : null); // 시술내용 id
        postDTO.setDirectParentTreatment(post.getParent() != null ? post.getParent().getName() : null); // 시술내용
        postDTO.setChildTreatment(post.getChild() != null ? post.getChild().getTreatmentId() : null); // 세부내용 id
        postDTO.setDirectChildTreatment(post.getChild() != null ? post.getChild().getName() : null); // 세부내용
        postDTO.setVisitId(post.getVisit() != null ? post.getVisit().getVisitId() : null); // 방문경로 id
        postDTO.setVisitPath(post.getVisit() != null ? post.getVisit().getVisitPath() : null); // 방문경로
        postDTO.setTreatmentDate(post.getTreatmentDate()); // 시술날짜
        postDTO.setRetouch(Boolean.valueOf(post.getRetouch())); // 리터치 여부
        postDTO.setRetouchDate(post.getRetouchDate()); // 리터치 날짜
        postDTO.setBeforeImageUrl(post.getBeforeImageUrl()); // 비포
        postDTO.setAfterImageUrl(post.getAfterImageUrl()); // 애프터
        postDTO.setInfo(post.getInfo()); // 비고

        return postDTO;
    }
    ```
    
    <h2>게시물을 바로 삭제하지 않고, 휴지통에 넣어서 복원/삭제 처리</h2>

    휴지통으로 보내기
    ```java
    public String postDelete(Long postId){
        Post post = postRepository.findById(postId).orElse(null);

        if (post != null) {
            // isDeleted 값을 "Y"로 변경
            post.setIsDeleted("Y");
            // 포스트 업데이트
            postRepository.save(post);
            return null;
        }
        return null;
    }
    ```

    <br>

    휴지통에서 복원하기
    ```java
    public String postRestore(Long postId){
        Post post = postRepository.findById(postId).orElse(null);

        if (post != null) {
            // isDeleted 값을 "N"로 변경
            post.setIsDeleted("N");
            // 포스트 업데이트
            postRepository.save(post);
            return null;
        }
        return null;
    }
    ```

    <br>

  </details>
  
  <details>
    <summary> 관계형 데이터베이스 전략으로 상위 시술내용에 따라 하위 시술내용 불러오기
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>
    
    ![시술내역 등록](https://github.com/user-attachments/assets/1f3cc6cf-35c2-40b2-9e7a-edc44d91f81e)

    <h2>상위 시술의 treatment_id값을 참조하는 관계형 데이터베이스 전략</h2>
    
    ```java
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long treatmentId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_treatmentId")
    private Treatment parent;

    private Long duration;
    ```
    ![관계형](https://github.com/user-attachments/assets/5dc25344-3782-4a96-b270-cb000043343e)

    
    <br>
  
    <h2>parent_treatmentId가 null인 목록을 모두 불러와서 상위 시술내용을 출력</h2>

    <br>
    
    Contoller
    ```java
    @GetMapping("/post/create")
      public String postCreateForm(Model model){
    
      List<Treatment> treatments = treatmentService.treatmentFindParent();
      model.addAttribute("treatments", treatments);
  
      return "post/postCreateForm";
    }
    ```
    Service
    ```java
    public List<Treatment> treatmentFindParent(){
        return treatmentRepository.findAllByParentIsNull();
    }
    ```

    <br>

    <h2>출력된 값을 fetch함수를 사용해 다시 서버로 보내서 해당하는 하위 시술내용 출력</h2>

    <br>

    ```java
    function updateChildTreatments() {
        const parentId = document.getElementById("parentTreatment").value;

        fetch(`/treatment/${parentId}/childTreatment`)
            .then(response => response.json())
            .then(data => {
                const subCategorySelect = document.getElementById("childTreatment");
                subCategorySelect.innerHTML = "";

                const defaultOption = document.createElement("option");
                defaultOption.value = "";
                defaultOption.text = "세부내용 선택";
                subCategorySelect.appendChild(defaultOption)

                console.log(data.forEach(subcategory => {
                    const id = subcategory.treatmentId;
                    const name = subcategory.name;
                    console.log("id "+id + " name "+name);
                }))
    ```
    
    Controller
    ```java
    @GetMapping("/treatment/{parentId}/childTreatment")
    @ResponseBody
    public List<Treatment> getChildTreatment(@PathVariable Long parentId) {
        return treatmentService.findChildTreatment(parentId);
    }
    ```
    Service
    ```java
    public List<Treatment> findChildTreatment(Long parentId) {
        return treatmentRepository.findAllByParent_TreatmentId(parentId);
    }
    ```


  </details>

- ### 예약기능
  <details>
    <summary> 사용자가 예약을 요청하면 관리자에게 확인메일 발송 (추후 문자로 변경)
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>
    
    Controller
    ```java
    @PostMapping("/reservation/create")
    public String reservationCreate(@ModelAttribute ReservationDTO reservationDTO, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        Long userId = sessionUser.getUserId();

        try {
            reservationService.reservationCreate(reservationDTO);

            try {
                String to = "**@**.com";
                String subject = "예약요청 메일입니다.";

                String name = reservationDTO.getName();
                String parentName = reservationDTO.getParentName();
                String childName = reservationDTO.getChildName();
                String date = String.valueOf(reservationDTO.getReservationDate());
                String startTime = String.valueOf(reservationDTO.getReservationStartTime());

                String text = name + "님  / " + date + " / " + startTime + " / " + parentName + "/" + childName + " ";

                mailService.sendMail(to, subject, text);
                return "redirect:/reservation/" + userId + "/ownList";
            } catch (MessagingException e){
                return "실패" + e.getMessage();
            }

        } catch (IllegalArgumentException e) {
            return "redirect:/reservation/create?exist=true";
        }

    }
    ```
    
    Service
    ```java
    public void sendMail(String to, String subject, String text) throws MessagingException{

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        // 이메일 설정
        helper.setTo(to); // 수신자
        helper.setSubject(subject); // 메일 제목
        helper.setText(text, true); // 내용, HTML 여부

        // 메일 발송
        mailSender.send(message);
    }
    ```
    
    ![메일](https://github.com/user-attachments/assets/90220dbb-6b46-479e-a1e5-6933e06f8d9e)

  </details> 

  <details>
    <summary> 예약상태 변경 기능
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>

    <h2>예약상태를 누르면 모달 창을 띄우고, reservation_id, reservation_state 정보를 담아 컨트롤러로 전송</h2>
  
    <br>
    
    ![예약상태변경1](https://github.com/user-attachments/assets/3335c1e8-cad0-4917-950e-9381e9c79746)
    
    View
    ```java
    // Html
    <div class="modal fade" id="reservationModal" tabindex="-1" aria-labelledby="reservationModalLabel" aria-hidden="true">
        <form id="modalAction" method="post">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="reservationModalLabel">예약상태 변경</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="modalReservationId">
                        <select class="form-select" name="modalReservationState" id="modalReservationState" aria-label="Default select example">
                            <option value="예약대기">예약대기</option>
                            <option value="예약확정">예약확정</option>
                            <option value="시술완료">시술완료</option>
                        </select>
                    </div>
                    <div class="modal-footer">
                          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                          <button type="submit" class="btn btn-primary">변경하기</button>
                    </div>
                </div>
            </div>
        </form>
    </div>

    // JavaScript
    function showReservationModal(modal) {
        const reservationId = modal.dataset.id;
        const reservationState = modal.dataset.state;
        const stateSelect = document.getElementById("modalReservationState");

        document.getElementById("modalReservationId").value = reservationId;

        if (reservationState === '예약대기') {
            stateSelect.value = '예약대기';
        } else if (reservationState === '예약확정') {
            stateSelect.value = '예약확정';
        } else if (reservationState === '시술완료') {
            stateSelect.value = '시술완료';
        }

        const form = document.getElementById("modalAction");
        form.action = '/reservation/'+reservationId+'/stateUpdate';

        var reservationModal = new bootstrap.Modal(document.getElementById("reservationModal"));
        reservationModal.show();
    }
    ```
    
    Controller
    ```java
    @PostMapping("/reservation/{reservationId}/stateUpdate")
    public String reservationStateUpdate(@PathVariable Long reservationId, ReservationDTO reservationDTO){

        reservationService.reservationStateUpdate(reservationId, reservationDTO);

        return "redirect:/reservation/list";
    }
    ```
    
    Service
    ```java
    public void reservationStateUpdate(Long reservationId, ReservationDTO reservationDTO) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약내역을 찾을 수 없습니다. :" + reservationId));
        
        reservation.setState(reservationDTO.getModalReservationState());

        reservationRepository.save(reservation);
    }
    ```
  </details> 

  <details>
    <summary>원하는 예약 시간을 선택하여 예약을 요청하면 선택한 시술에따라 자동으로 예약 종료시간 설정
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>
    
    <h2>각 시술마다 소요시간 필드를 추가하여 LocalTime 타입으로 변환 후 servation_startTime과 연산하여 endTime 설정</h2>

    ![image](https://github.com/user-attachments/assets/e3df8a9e-01fa-47b7-886d-0a67910485bc)
    ![image](https://github.com/user-attachments/assets/ebf3706b-eda3-47d9-92cc-9b2888476020)

    <br>
      
    Service
    ```java
    LocalTime endTime = null;
    if (childTreatment != null) {
        reservation.setChild(childTreatment); // 소분류 설정
        Long duration = childTreatment.getDuration();
        endTime = startTime.plusMinutes(duration);
    }
    ```
    
    <br>
    겹치는 시간에 대한 예외처리
    
    ```java
    if (isReservationOverlapping(date, startTime, endTime)) {
        throw new IllegalArgumentException("해당 시간에 이미 다른 예약이 있습니다.");
    }
    ```

    ```java
    // 예약 시간 겹침 확인 메서드
    public boolean isReservationOverlapping(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return reservationRepository.existsOverlappingReservation(date, startTime, endTime);
    }
    ```
    
  </details>
  
  <details>
    <summary> 날짜별 예약가능 시간 표시
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
    <br>
    
    <h2>날짜를 선택하면 해달 날짜에 있는 모든 예약시간을 조회 후, 시작시간 / 종료시간 사이에 있는 시간들은 비활성화 처리 </h2>
  
    <br>
    
    ![날짜별 예약가능시간2](https://github.com/user-attachments/assets/97b5ea10-063b-4932-917c-880c1aba8766)

    <br>

    View
    ```java
    function reservationTime(inputElement) {
        const selectedDate = inputElement.value;
        console.log(selectedDate);

        fetch('/reservation/timeCheck', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ reservationDate: selectedDate })
        })
            .then(response => response.json())
            .then(existTime => {
                console.log(existTime);
    .
    .
    .
    .
    .
    ```
    
    Controller
    ```java
    @PostMapping("/reservation/timeCheck")
    public ResponseEntity<List<String>> reservationTimeCheck(@RequestBody Map<String, String> request){

        String selectedDate = request.get("reservationDate");

        List<String> existTime = reservationService.reservationTimeCheck(LocalDate.parse(selectedDate));

        return ResponseEntity.ok(existTime);
    }
    ```
    
    Service
    ```java
    public List<String> reservationTimeCheck(LocalDate  selectedDate){

        List<Reservation> reservations = reservationRepository.findByReservationDate(selectedDate);

        List<String> existTime = new ArrayList<>();
        for (Reservation reservation : reservations) {
            existTime.add(String.valueOf(reservation.getReservationStartTime()));
            existTime.add(String.valueOf(reservation.getReservationEndTime()));
        }

        return existTime;
    }
    ```
  </details> 

  
- ### 리뷰기능 (추가 예정)
  
- ### 배포
  <details>
    <summary>Docker에 Tomcat을 설치하여 war 방식으로 배포</summary>
  </details>
  <details>
    <summary>Synology에서 지원하는 기본 도메인 사용</summary>
  </details>
<br><br>

# 🔥 문제점 및 해결방안

<br><br>

