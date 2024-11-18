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
### 🤨 [AutumnBrow](http://autumnbrow.hhjnn92.synology.me:8080/)
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
    <summary> Synology FTP서버에 이미지 업로드 처리 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
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
              ftpUtil.uploadFile("/AutumnBrow/BeforeAndAfter/" + uniqueFileName, localFile);
  
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
    <summary> Synology에 데이터베이스 서버 구축 (MariaDB 10) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
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
  - ERD 작성    
  - 속성 / 관계 정의
  - 정규화

- ### RESTful API
- ### 회원 CRUD
  <details>
    <summary> 사용자가 직접 회원가입할 때 이름, 전화번호, 생년월일 정보만 받아서 <br>
    &nbsp;&nbsp;&nbsp;회원가입을 진행하지만전화번호를 암호화처리 해서 비밀번호로도 사용 가능 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
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
    <summary> 회원가입이 되어있지 않을 때, 관리자가 시술 내역을 등록하면 회원가입까지 같이 진행됨. &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
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
  
    - Spring Security 적용 예정

- ### 관리자용 시술내역, 공지사항, 주의사항, 이벤트 등 커뮤니티 CRUD
  <details>
    <summary> 관계형 데이터베이스 전략으로 상위 시술내용에 따라 하위 시술내용 불러오기 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(코드▼)</summary>
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

  - 예약기능 (구현중)
  - 리뷰기능 (추가 예정)
<br><br>

# 🔥 개선사항
<br><br>

