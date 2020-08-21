package com.ellie.billiardsgame

//----------------------------------------------------------
// Constant definitions.
//

// 프레임 간격 (ms)
const val FRAME_DURATION_MS = 10L

// 안내선의 최대 길이
const val MAX_GUIDELINE_LENGTH = 800f

// 공의 최대 속력
const val MAX_POWER = FRAME_DURATION_MS * 5f

// 공이 멈췄다고 판단하는 임계값
const val STOP_THRESHOLD = FRAME_DURATION_MS * 0.05f

// Ball Id - Index 역할
const val WHITE = 0
const val RED1 = 1
const val RED2 = 2