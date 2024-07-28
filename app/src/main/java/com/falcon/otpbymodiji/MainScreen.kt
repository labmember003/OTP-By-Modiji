package com.falcon.otpbymodiji

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun MainScreen(viewModel: MainViewModel, networkUtils: NetworkUtils) {
    val context = LocalContext.current
    val mobile = remember { mutableStateOf("") }
    val count = remember { mutableStateOf("") }

    var contactName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var contactUri by remember { mutableStateOf<Uri?>(null) }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                contactUri = uri
                val (name, number) = getContactDetails(context, uri)
                contactName = name

                contactNumber = getFormatedPhoneNumber(number)
                mobile.value = contactNumber
            } else {
                Toast.makeText(context, "Invalid Contact", Toast.LENGTH_LONG).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            contactPickerLauncher.launch(intent)
        } else {
            Toast.makeText(context, "Contacts Permission Denied", Toast.LENGTH_LONG).show()
        }
    }



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        MainScreenHeader()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = mobile.value,
                onValueChange = { mobile.value = it },
                label = { Text("Mobile Number") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        requestContactsPermission(context, permissionLauncher) {
                            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                            contactPickerLauncher.launch(intent)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ContactPhone,
                            contentDescription = "Contacts Icon"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = count.value,
                onValueChange = { count.value = it },
                label = { Text("Number of OTPs") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
            Spacer(modifier = Modifier.height(60.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = {
                    val mobileInput = getFormatedPhoneNumber(mobile.value)
                    val countInput = count.value.toIntOrNull()
                    if (countInput == null) {
                        Toast.makeText(context, "Please enter a valid count", Toast.LENGTH_LONG).show()
                    } else if (countInput <= 0) {
                        Toast.makeText(context, "Please enter a valid count", Toast.LENGTH_LONG).show()
                    } else if (countInput > 100) {
                        Toast.makeText(context, "Please enter a valid number less than 100", Toast.LENGTH_LONG).show()
                    } else if (!networkUtils.isNetworkAvailable(context)) {
                        Toast.makeText(context, "No Internet Connectivity", Toast.LENGTH_LONG).show()
                    } else if (mobileInput.length != 10) {
                        Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_LONG).show()
                    } else {
                        viewModel.registerAndSendOtp(mobileInput, countInput)
                    }
                }) {
                Text("Start")
            }
            Spacer(modifier = Modifier.height(16.dp))
            StatusText(message = viewModel.uiState.collectAsState().value.statusMessage)
        }
    }

}

fun getFormatedPhoneNumber(number: String): String {
    return number.filter { it.isDigit() }
}


@Composable
private fun MainScreenHeader() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 25.dp, 16.dp, 0.dp)
    ) {
        Text(
            text = "OTP By Modiji",
            fontSize = 28.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

private fun getContactDetails(context: Context, uri: Uri): Pair<String, String> {
    var contactName = ""
    var contactNumber = ""
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    if (cursor != null && cursor.moveToFirst()) {
        val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
        if (nameIndex != -1) {
            contactName = cursor.getString(nameIndex)
        }

        val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
        val contactId = cursor.getString(idIndex)

        val hasPhoneNumberIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
        val hasPhoneNumber = cursor.getInt(hasPhoneNumberIndex)

        if (hasPhoneNumber > 0) {
            val phoneCursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                arrayOf(contactId),
                null
            )
            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                contactNumber = phoneCursor.getString(numberIndex)
                phoneCursor.close()
            }
        }
        cursor.close()
    }
    return contactName to contactNumber
}



fun requestContactsPermission(
    context: Context,
    permissionLauncher: ActivityResultLauncher<String>,
    onPermissionGranted: () -> Unit
) {
    when {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED -> run {
            onPermissionGranted()
        }
        else -> {
            permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }
}
