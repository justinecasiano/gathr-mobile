package com.example.gathr.core.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.gathr.R
import com.example.gathr.ui.theme.AppFonts

@Composable
fun PhotoUploadComposable(
    selectedImageUriString: String?,
    onImageSelected: (String?) -> Unit,
    onImageClick: (String) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        onImageSelected(uri?.toString())
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (selectedImageUriString != null && selectedImageUriString.isNotBlank()) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = selectedImageUriString,
                    contentDescription = "Uploaded Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(selectedImageUriString) }
                )

                Box(
                    Modifier
                        .padding(top = 10.dp, end = 10.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .size(20.dp)
                            .clickable { onImageSelected("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = "Remove photo",
                            tint = Color(0xFF8C44AA),
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF56387D), CircleShape)
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_photo_icon),
                        contentDescription = "Add photo",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Add photo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFonts.rethinkSans,
                    color = Color.Black.copy(0.8f)
                )
            }
        }
    }
}
