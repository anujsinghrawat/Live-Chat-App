package com.example.livechatapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.livechatapp.R

@Composable
fun MyTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    label :String ="",
    modifier: Modifier = Modifier,
    isNumber: Boolean = false,
    isPassword:Boolean = false,

) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value ,
        onValueChange = {
            onValueChanged(it)
        },
        modifier = modifier,
        label = {
            Text(text = label)
        } ,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text
        ),
        visualTransformation = if (isPassword) {
            if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        } else{
            VisualTransformation.None
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible },
                    modifier = Modifier.clickable { }
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                    )
                }
            }
        }

    )
}

@Composable
fun MyButton(
    label : String ,
    onClick : () ->Unit,
    modifier: Modifier = Modifier

) {
    Button(
        onClick = onClick,
        modifier = modifier) {
        Text(text = label)
    }

}
