package ch.heigvd.iict.daa.rest.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ch.heigvd.iict.daa.rest.ContactsApplication
import ch.heigvd.iict.daa.rest.R
import ch.heigvd.iict.daa.rest.models.Contact
import ch.heigvd.iict.daa.rest.models.PhoneType
import ch.heigvd.iict.daa.rest.ui.theme.MyComposeApplicationTheme
import ch.heigvd.iict.daa.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.daa.rest.viewmodels.ContactsViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Main composable for editing contacts.
 *
 * @param contactsViewModel the view model to use
 * @param contact optionally, an existing contact to edit
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
@Composable
fun ScreenContactsEditor(
    contactsViewModel: ContactsViewModel,
    contact: Contact?,
    onNavigateBack: () -> Unit
) {
    var contactName by remember { mutableStateOf(contact?.name) }
    var contactFirstname by remember { mutableStateOf(contact?.firstname) }
    var contactAddress by remember { mutableStateOf(contact?.address) }
    var contactBirthday by remember { mutableStateOf(contact?.birthday) }
    var contactEmail by remember { mutableStateOf(contact?.email) }
    var contactZip by remember { mutableStateOf(contact?.zip) }
    var contactCity by remember { mutableStateOf(contact?.city) }
    var contactType by remember { mutableStateOf(contact?.type) }
    var contactPhone by remember { mutableStateOf(contact?.phoneNumber) }

    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.form_field_padding))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement
            .spacedBy(dimensionResource(R.dimen.form_field_padding))
    ) {
        Text(text = stringResource(contact?.let { R.string.screen_detail_title_edit }
            ?: R.string.screen_detail_title_new))

        ContactTextField(
            stringResource(R.string.screen_detail_name_subtitle),
            contactName,
            onValueChange = { contactName = it }
        )
        ContactTextField(
            stringResource(R.string.screen_detail_firstname_subtitle),
            contactFirstname,
            onValueChange = { contactFirstname = it }
        )
        ContactTextField(
            stringResource(R.string.screen_detail_email_subtitle),
            contactEmail,
            onValueChange = { contactEmail = it }
        )
        ContactDateField(
            stringResource(R.string.screen_detail_birthday_subtitle),
            contactBirthday,
            onValueChange = { contactBirthday = it }
        )
        ContactTextField(
            stringResource(R.string.screen_detail_address_subtitle),
            contactAddress ?: "",
            onValueChange = { contactAddress = it }
        )
        ContactTextField(
            stringResource(R.string.screen_detail_zip_subtitle),
            contactZip ?: "",
            onValueChange = { contactZip = it }
        )
        ContactTextField(
            stringResource(R.string.screen_detail_city_subtitle),
            contactCity ?: "",
            onValueChange = { contactCity = it }
        )

        ContactPhoneTypeField(
            selected = contactType,
            onValueChange = { contactType = it }
        )

        ContactTextField(
            stringResource(R.string.screen_detail_phonenumber_subtitle),
            contactPhone?: "",
            onValueChange = { contactPhone = it }
        )
        EditButtons(
            contactsViewModel, contact == null,
            Contact(
                name = contactName ?: "",
                firstname = contactFirstname,
                address = contactAddress,
                zip = contactZip,
                city = contactCity,
                phoneNumber = contactPhone,
                type = contactType,
                birthday = contactBirthday,
                email = contactEmail,
                serverId = contact?.serverId,
            ), onNavigateBack
        )
    }

}

@Composable
private fun ContactTextField(
    label: String,
    fieldValue: String?,
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
        )
        TextField(
            value = fieldValue ?: "",
            onValueChange = onValueChange,
        )
    }
}

@Composable
fun ContactDateField(
    label: String,
    value: Calendar?,
    onValueChange: (Calendar) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    if (showDialog) {
        val datePickerDialog = DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                    onValueChange(this)
                }
                showDialog = false
            },
            value?.get(Calendar.YEAR) ?: Calendar.getInstance().get(Calendar.YEAR),
            value?.get(Calendar.MONTH) ?: Calendar.getInstance().get(Calendar.MONTH),
            value?.get(Calendar.DAY_OF_MONTH) ?: Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        TextField(
            value = value?.let { dateFormatter.format(it.time) } ?: "",
            onValueChange = { },
            readOnly = true,
            enabled = false, // workaround to make the field clickable
            modifier = Modifier
                .clickable {
                    showDialog = true
                },

            )
    }
}

@Composable
private fun ContactPhoneTypeField(
    selected: PhoneType?,
    onValueChange: (PhoneType) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.screen_detail_phonetype_subtitle),
        )
        Row {
            PhoneType.entries.forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected == type,
                        onClick = { onValueChange(type) }
                    )
                    Text(
                        text = stringResource(
                            when (type) {
                                PhoneType.HOME -> R.string.phonetype_home
                                PhoneType.OFFICE -> R.string.phonetype_office 
                                PhoneType.MOBILE -> R.string.phonetype_mobile
                                PhoneType.FAX -> R.string.phonetype_fax
                            }
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EditButtons(
    viewModel: ContactsViewModel,
    isNew: Boolean,
    editedContact: Contact,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onNavigateBack) {
            Text(text = "CANCEL")
        }

        if (!isNew) {
            Button(onClick = {
                viewModel.delete(editedContact!!)
                onNavigateBack()
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.padding(end = dimensionResource(R.dimen.list_item_elements_padding))
                )
                Text(text = "DELETE")
            }

            Button(onClick = {
                viewModel.update(editedContact!!)
                onNavigateBack()
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.padding(end = dimensionResource(R.dimen.list_item_elements_padding))
                )
                Text(text = "SAVE")
            }
        } else {
            Button(onClick = {
                viewModel.create(
                    editedContact!!
                )
                onNavigateBack()
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.padding(end = dimensionResource(R.dimen.list_item_elements_padding))
                )
                Text(text = "CREATE")
            }
        }
    }
}

val demo = Contact(
    name = "Doe",
    firstname = "John",
    address = "Rue de la Source 12",
    zip = "1000",
    city = "Lausanne",
    phoneNumber = "021 123 45 67",
    type = PhoneType.MOBILE,
    birthday = Calendar.getInstance().apply {
        set(1990, 1, 1)
    },
    email = "john@doe.com"
)


@Preview(showBackground = true)
@Composable
fun ContactEditorPreview() {
    MyComposeApplicationTheme {
        val viewModel =
            ContactsViewModelFactory(ContactsApplication()).create(ContactsViewModel::class.java)
        ScreenContactsEditor(viewModel, demo) {}
    }
}

