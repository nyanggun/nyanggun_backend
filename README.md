# 한국 문화 사냥꾼 (Korea Culture Hunters)

> **문화재 기반 실감형 즐길거리 & 정보 공유 플랫폼**
<div align="center">
<img width="200" height="250" alt="한국문화사냥꾼 로고" src="https://github.com/user-attachments/assets/e27714a9-9c61-410d-9698-faa7c3c8bfc9" />
</div>
    
## 🗺 스크린샷
<div align="center">
    <img width="200"  alt="image 9" src="https://github.com/user-attachments/assets/6e57d9db-1682-4ec1-b486-2e69ff96b5f5" />
    <img width="200"  alt="image 10" src="https://github.com/user-attachments/assets/08b46e9f-971d-4554-aa36-d9895404f9aa" />
    <img width="200"  alt="image 12" src="https://github.com/user-attachments/assets/9d62a4c1-f0d7-426f-9248-2bf7326db8a7" />
    <img width="200"  alt="image 13" src="https://github.com/user-attachments/assets/1eab4802-2a7b-4646-a1b4-5cf1949052eb" />
</div>

## 시연 영상
### [전체 시연 영상 링크(유투브 링크)](https://youtu.be/fEZU-rIgKQM)
<p align="center">
  <img src="https://github.com/user-attachments/assets/30d4ba37-0f00-44a0-acc8-fc97c451e29b" width="250" alt="1" />
  <img src="https://github.com/user-attachments/assets/ee5eaaec-506c-40d7-bdcf-b44de4e5a754" width="250" alt="2" />
  <img src="https://github.com/user-attachments/assets/493aee56-e89e-4b8d-ab0b-d32ede3bc287" width="250" alt="3" />
</p>
<p align="center">
  <img src="https://github.com/user-attachments/assets/1cb11779-2dfc-428e-8e5f-2ab5daecea7b" width="250" alt="4" />
  <img src="https://github.com/user-attachments/assets/09e74ee8-d4ad-4d3d-82ce-fae0413cf6ff" width="250" alt="5" />
  <img src="https://github.com/user-attachments/assets/79dc7ce4-66de-49b4-a700-58f7d8887fb6" width="250" alt="6" />
</p>


---

## 🧭 프로젝트 소개

**한국 문화 사냥꾼(냥꾼)** 은 한국의 문화재를 기반으로 **게임화**하여 탐방을 유도하고, 문화유산청 API를 활용해 지도와 도감으로 정보를 제공하며, 커뮤니티에서 탐방기와 담소를 나눌 수 있게 만든 문화 체험 플랫폼입니다.
한국을 찾는 외국 관광객과 국내 문화재 애호가 모두를 위한 **즐길거리 + 정보 + 커뮤니티** 경험을 제공합니다.

### 🎯 서비스 목적
* 문화재 기반 **즐길거리** 제공 (증표 컬렉팅)
* 도감과 지도 통한 **문화재 정보 제공**
* 게시판/댓글을 통한 **커뮤니티**

### 👥 서비스 대상
* 대한민국 관광객 누구나
* K‑Culture를 좋아하는 외국인
* 문화재·역사에 관심 있는 사람
* 관련 산업 종사자(해설·교육·연구 등)

### 📈 배경
K‑Culture 유행으로 한국 방문 외국인이 증가하고 있습니다. 냥꾼은 **게임적 보상(증표)** 과 **정보접근성**을 결합해 문화재 경험을 한층 풍부하게 만듭니다.

---

## 🧩 핵심 기능
* **증표(뱃지) 컬렉팅**: 사용자가 문화재 반경 내에 진입하면 증표 획득 가능
* **문화재 정보 제공**: 국가유산청 API를 활용한 정확한 문화재 정보 제공
* **커뮤니티**: 게시글/댓글로 후기와 의견 공유

---

## 🏗 아키텍처
<img width="800" height="673" alt="architecture diagram" src="https://github.com/user-attachments/assets/4889943f-45f2-4e1d-a3df-b978ac398d61" />

* **Frontend** : React + Vite, Axios
* **Backend** : Spring Boot, Spring Data JPA, Spring Security(JWT)
* **DB** : MySQL (RDS)
* **CI/CD** : GitHub Actions,  AWS (EC2/Amplify, RDS, S3)

---

## 👣 개발 프로세스
### Agile 개발 로드맵
<img width="700" alt="개발로드맵 스크린샷" src="https://github.com/user-attachments/assets/c280e76a-5cd3-47a8-bfe1-9a7f23086534" />

* **Scrum**으로 5회 스프린트 운영: 계획 → 개발/리뷰 → 회고

### 서비스 기획
* **린캔버스 → 아이템 기획서 → IA → 아이디어 스케치 → 요구사항 정의서 → Use Case**
<img width="700" alt="기획 스크린샷" src="https://github.com/user-attachments/assets/a7fb2ee0-235d-49c4-acd3-29b3a1e0b606" />

### 분석/설계
<img width="700" alt="분석설계 스크린샷" src="https://github.com/user-attachments/assets/3336bd74-65d7-4648-85a4-d72a334952fc" />

* Frontend : User Story →  Wireframe → Prototype
* Backend : ERD → Class Diagram → Architecture Diagram

---

## 👩‍💻 팀

* 김남주 : 이슈관리리더, 스터디리더, 메인 디벨로퍼
* 주순형 : 프로젝트 리더, 도큐먼트 리더, 메인 디벨로퍼
* 김혜경 : 프로젝트 매니저, 메인 디벨로퍼
* 김기영 : QA리더, 형상관리리더, 메인 디벨로퍼
