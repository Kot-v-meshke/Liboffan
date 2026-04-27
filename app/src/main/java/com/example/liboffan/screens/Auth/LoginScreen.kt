package com.example.liboffan.screens.Auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Простая валидация
    val isFormValid = email.value.isNotBlank() && password.value.length >= 6

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7065AC),
                        Color(0xFF97A1EF),
                        Color(0xFFAB98EC)
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Добро пожаловать!",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Email
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = getTextFieldColors(),
            textStyle = TextStyle(color = Color.White),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Пароль
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Пароль") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) {
                    Icons.Default.Visibility
                } else {
                    Icons.Default.VisibilityOff
                }
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Color.White)
                }
            },
            colors = getTextFieldColors(),
            textStyle = TextStyle(color = Color.White),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onNavigateToForgotPassword,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Забыли пароль?", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                Log.d("LOGIN_DEBUG", "Кнопка 'Войти' нажата")
                Log.d("LOGIN_DEBUG", "Email: ${email.value}, Password length: ${password.value.length}")

                if (isFormValid) {
                    Log.d("LOGIN_DEBUG", "Форма валидна → вызываем onLogin")
                    onLogin(email.value, password.value)
                } else {
                    Log.d("LOGIN_DEBUG", "Форма НЕ валидна")
                }
                if (isFormValid) {
                    onLogin(email.value, password.value)
                }
            },
            enabled = isFormValid,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) Color(0xFF97A1EF) else Color(0xFF97A1EF).copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Войти", color = Color.White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Регистрация
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Нет аккаунта?", color = Color.White)
            TextButton(onClick = onNavigateToRegister) {
                Text("Зарегистрироваться", color = Color(0xFFF6F4FF))
            }
        }
    }
}

// функция для стилей полей
@Composable
fun getTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
    focusedBorderColor = Color(0xFFBEC5FF),
    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
    focusedLabelColor = Color(0xFFBEC5FF),
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    cursorColor = Color.White
)