# billiards-game

## 개요

당구(4구) 게임 시뮬레이션 (But, 노란공 X, 회전 X)

## Preview

| 준비 모드 | 실행 모드 | 편집 모드 |
|:--:|:--:|:--:|
| <img src="https://user-images.githubusercontent.com/29790944/90950346-66880a00-e48b-11ea-966d-44c6e9046aa9.png" width="80%"> | <img src="https://user-images.githubusercontent.com/29790944/90950304-2759b900-e48b-11ea-9043-3713faf841a7.png" width="80%"> | <img src="https://user-images.githubusercontent.com/29790944/90950396-ec0bba00-e48b-11ea-863a-5527ea77038b.png" width="80%">

## 시작하기

### Spec

Android Studio : 4.0.1

Android SDK : 29

### 설치

이 샘플은 Gradle 빌드 시스템을 사용합니다.

이 저장소를 복제해서 **Android Studio**에 import 합니다.

```bash
git clone https://github.com/foreknowledge/BilliardsGame.git
```

## 사용법

![image](https://user-images.githubusercontent.com/29790944/90950926-1f047c80-e491-11ea-8be5-36a7280b89e4.png)

### 추가 설명

- 안내선으로 공 방향 & 세기 조절.

- **Fling On/Off** - 흰 공 Fling 모션으로 실행할지에 대한 스위치.
  - **On** - Fling으로 시뮬레이션 실행. (안내선 X)
  - **Off** - 안내선 & 버튼으로 시뮬레이션 실행.
  
- 실행 모드에서 터치 불가능. 
  - 모든 공이 멈추면 **준비 모드**로 전환.
  - **CANCEL 버튼** - 시뮬레이션 취소. 공 원위치로 돌아가고 **준비 모드**로 전환.
