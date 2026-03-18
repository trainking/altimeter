package com.altimeter.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altimeter.utils.AltitudeCalculator
import com.altimeter.viewmodel.AltitudeViewModel

@Composable
fun AltimeterScreen(viewModel: AltitudeViewModel, onSettingsClick: () -> Unit) {
    val altitude by viewModel.currentAltitude.observeAsState(0f)
    val pressure by viewModel.currentPressure.observeAsState(0f)
    val accuracy by viewModel.accuracy.observeAsState(0f)
    val useFeet by viewModel.useFeet.observeAsState(false)
    val azimuth by viewModel.azimuth.observeAsState(0f)

    val unit = if (useFeet) "ft" else "m"
    val displayAltitude = if (useFeet) {
        AltitudeCalculator.metersToFeet(altitude)
    } else {
        altitude
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4ECDC4),
                        Color(0xFF58B96A),
                        Color(0xFFF4D03F),
                        Color(0xFFF39C12)
                    ),
                    startY = 0f,
                    endY = 1000f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部标题
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "实时高度表",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 气压和精度信息
            if (pressure > 0) {
                Text(
                    text = "${String.format("%.2f", pressure)} kPa",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            if (accuracy > 0) {
                Text(
                    text = "海拔精度:${accuracy.toInt()} 米",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 主高度显示 - 圆形气泡带指南针刻度环
            Box(
                modifier = Modifier
                    .size(320.dp),
                contentAlignment = Alignment.Center
            ) {
                // 指南针刻度环（随方位角旋转）
                CompassRing(
                    azimuth = azimuth,
                    modifier = Modifier
                        .size(320.dp)
                        .rotate(azimuth)
                )

                // 红色气泡
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .background(
                            color = Color(0xFFE74C3C),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "当前海拔",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = displayAltitude.toInt().toString(),
                                style = MaterialTheme.typography.displayLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 80.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = unit,
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // GPS 坐标信息（占位）
            Text(
                text = "北纬 23°7'18\"",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Text(
                text = "东经 113°22'5\"",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.weight(1f))

            // 底部按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.toggleUnit() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text(
                        text = if (useFeet) "切换为米" else "切换为英尺",
                        color = Color.White
                    )
                }

                Button(
                    onClick = onSettingsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text(
                        text = "校准",
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CompassRing(azimuth: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 绘制刻度线和背景环
        Canvas(
            modifier = Modifier.size(320.dp)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.width / 2

            // 绘制背景圆环
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = radius,
                center = Offset(centerX, centerY)
            )

            // 绘制 360 度刻度（每 2 度一条）
            for (i in 0 until 360 step 2) {
                val angleRad = Math.toRadians(i.toDouble())
                val isMainMark = i % 30 == 0
                val isCardinalMark = i % 90 == 0

                val markLength = when {
                    isCardinalMark -> 20.dp.toPx()
                    isMainMark -> 12.dp.toPx()
                    else -> 6.dp.toPx()
                }

                val markWidth = when {
                    isCardinalMark -> 3.dp.toPx()
                    isMainMark -> 2.dp.toPx()
                    else -> 1.dp.toPx()
                }

                val startX = centerX + (radius - markLength) * kotlin.math.cos(angleRad - Math.PI / 2).toFloat()
                val startY = centerY + (radius - markLength) * kotlin.math.sin(angleRad - Math.PI / 2).toFloat()
                val endX = centerX + radius * kotlin.math.cos(angleRad - Math.PI / 2).toFloat()
                val endY = centerY + radius * kotlin.math.sin(angleRad - Math.PI / 2).toFloat()

                val color = when {
                    isCardinalMark -> Color.White.copy(alpha = 0.9f)
                    isMainMark -> Color.White.copy(alpha = 0.6f)
                    else -> Color.White.copy(alpha = 0.3f)
                }

                drawLine(
                    color = color,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = markWidth,
                    cap = StrokeCap.Round
                )
            }
        }

        // 方向字母（位于方向环外侧）
        Text(
            text = "北",
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-24).dp)
        )

        Text(
            text = "东",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 20.dp)
        )

        Text(
            text = "南",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 20.dp)
        )

        Text(
            text = "西",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-20).dp)
        )
    }
}
