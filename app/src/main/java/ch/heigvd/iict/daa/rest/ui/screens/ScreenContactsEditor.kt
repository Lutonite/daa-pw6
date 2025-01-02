package ch.heigvd.iict.daa.rest.ui.screens

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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.heigvd.iict.daa.rest.ContactsApplication
import ch.heigvd.iict.daa.rest.R
import ch.heigvd.iict.daa.rest.models.Contact
import ch.heigvd.iict.daa.rest.models.PhoneType
import ch.heigvd.iict.daa.rest.ui.theme.MyComposeApplicationTheme
import ch.heigvd.iict.daa.rest.viewmodels.ContactsViewModel
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

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
    contactsViewModel: ContactsViewModel?,
    contact: Contact?,
) {
    var contactName by remember { mutableStateOf(contact?.name) }
    var contactFirstname by remember { mutableStateOf(contact?.firstname) }
    var contactAddress by remember { mutableStateOf(contact?.address) }
    var contactBirthday by remember { mutableStateOf(contact?.birthday) }
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
        EditButtons()
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
private fun ContactDateField(
    label: String,
    fieldValue: Calendar?,
    onValueChange: (Calendar) -> Unit,
) {
    fun formatDate(calendar: Calendar) = DateFormat
        .getDateInstance(DateFormat.SHORT)
        .format(calendar.time)

    fun parseDate(date: String) = Calendar.getInstance().apply {
        time = DateFormat.getDateInstance(DateFormat.SHORT).parse(date) ?: Date()
    }

    var textValue by remember { mutableStateOf(fieldValue?.let { formatDate(it) } ?: "") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        ) {
        Text(text = label)
        TextField(
            value = textValue,
            onValueChange = {
                textValue = it
                onValueChange(parseDate(it))
            }
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
private fun EditButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly)
    {
        Button(
            onClick = {  }) {
            Text(text = "CANCEL")
        }
        Button(
            onClick = { /*TODO*/ },
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.padding(end =
                dimensionResource(
                R.dimen.list_item_elements_padding)
                )
            )
            Text(text = "DELETE")
        }

        Button(
            onClick = { /*TODO*/ },
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.padding(end =
                dimensionResource(
                    R.dimen.list_item_elements_padding)
                )
            )
            Text(text = "SAVE")
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
        ScreenContactsEditor(null, demo)
    }
}

