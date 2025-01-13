package ch.heigvd.iict.daa.rest.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ch.heigvd.iict.daa.rest.R
import ch.heigvd.iict.daa.rest.models.Contact
import ch.heigvd.iict.daa.rest.models.PhoneType
import ch.heigvd.iict.daa.rest.models.description
import ch.heigvd.iict.daa.rest.ui.composables.fontDimensionResource
import ch.heigvd.iict.daa.rest.viewmodels.ContactsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Main composable for creating or editing contacts.
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
    onQuit: () -> Unit
) {
    val context = LocalContext.current

    var contactName by rememberSaveable { mutableStateOf(contact?.name) }
    var contactFirstname by rememberSaveable { mutableStateOf(contact?.firstname) }
    var contactEmail by rememberSaveable { mutableStateOf(contact?.email) }
    var contactBirthday by rememberSaveable { mutableStateOf(contact?.birthday) }
    var contactAddress by rememberSaveable { mutableStateOf(contact?.address) }
    var contactZip by rememberSaveable { mutableStateOf(contact?.zip) }
    var contactCity by rememberSaveable { mutableStateOf(contact?.city) }
    var contactType by rememberSaveable { mutableStateOf(contact?.type) }
    var contactPhoneNumber by rememberSaveable { mutableStateOf(contact?.phoneNumber) }

    fun buildContact(): Contact? {
        if (contactName.isNullOrBlank()) {
            return null
        }

        return Contact(
            contact?.id,
            contactName!!,
            contactFirstname,
            contactBirthday,
            contactEmail,
            contactAddress,
            contactZip,
            contactCity,
            contactType,
            contactPhoneNumber,
            contact?.serverId,
            contact?.state ?: Contact.State.CREATED
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.form_field_padding))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement
            .spacedBy(dimensionResource(R.dimen.form_field_spacing))
    ) {
        Text(
            text = stringResource(
                contact
                    ?.let { R.string.screen_detail_title_edit }
                    ?: R.string.screen_detail_title_new
            ),
            fontSize = fontDimensionResource(R.dimen.form_title_size),
        )

        ContactTextField(
            stringResource(R.string.screen_detail_name_subtitle),
            contactName,
            mandatory = true,
            onValueChange = { contactName = it }
        )
        ContactTextField(
            stringResource(R.string.screen_detail_firstname_subtitle),
            contactFirstname,
            onValueChange = { contactFirstname = it }
        )
        ContactTextField(
            stringResource(R.string.screen_detail_email_subtitle),
            contactEmail ?: "",
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
        PhoneTypeRadioGroup(
            stringResource(R.string.screen_detail_phonetype_subtitle),
            contactType,
            onValueChange = { contactType = it }
        )
        ContactTextField(
            stringResource(R.string.screen_detail_phonenumber_subtitle),
            contactPhoneNumber ?: "",
            onValueChange = { contactPhoneNumber = it }
        )

        val fieldsMandatoryError = stringResource(R.string.toast_error_fields_mandatory)
        ButtonsRow(
            contact != null
        ) { action ->
            if (action == ButtonAction.CANCEL) {
                onQuit()
                return@ButtonsRow
            }

            val newContact = buildContact()
            if (newContact == null) {
                Toast.makeText(
                    context,
                    fieldsMandatoryError,
                    Toast.LENGTH_SHORT
                ).show()

                return@ButtonsRow
            }

            when (action) {
                ButtonAction.CREATE -> contactsViewModel.create(newContact)
                ButtonAction.UPDATE -> contactsViewModel.update(newContact)
                ButtonAction.DELETE -> contactsViewModel.delete(newContact)
                else -> {}
            }

            onQuit()
        }
    }
}

@Composable
private fun ContactTextField(
    label: String,
    fieldValue: String?,
    mandatory: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    var errorState by remember { mutableStateOf(false) }
    if (mandatory) {
        errorState = fieldValue.isNullOrBlank()
    }

    fun onChange(value: String) {
        errorState = mandatory && value.isBlank()
        onValueChange(value)
    }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        value = fieldValue ?: "",
        onValueChange = { onChange(it) },
        isError = errorState,
        supportingText = {
            if (errorState) {
                Text(
                    text = stringResource(R.string.screen_detail_error_mandatory),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
    )
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
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.form_field_padding)),
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.decorated_box_spacing))
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            label = { Text(text = label) },
            value = value?.let { dateFormatter.format(it.time) } ?: "",
            onValueChange = { },
            readOnly = true,
        )
        Button(
            shape = MaterialTheme.shapes.extraSmall,
            modifier = Modifier.fillMaxHeight(),
            contentPadding = PaddingValues(0.dp),
            onClick = { showDialog = true }
        ) {
            Image(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.screen_detail_btn_edit),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
        }
    }
}

@Composable
private fun PhoneTypeRadioGroup(
    label: String,
    fieldValue: PhoneType?,
    onValueChange: (PhoneType) -> Unit,
) {
    val choices = PhoneType.entries.toTypedArray()

    var selected by remember { mutableStateOf(fieldValue) }
    fun setSelected(type: PhoneType) {
        selected = type
        onValueChange(type)
    }

    Column {
        Text(text = label)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.form_field_padding))
                .horizontalScroll(rememberScrollState())
        ) {
            choices.forEach { type ->
                RadioButton(selected = selected == type, onClick = { setSelected(type) })
                Text(text = type.description())
            }
        }
    }
}

private enum class ButtonAction { CANCEL, CREATE, UPDATE, DELETE }

@Composable
private fun ButtonsRow(
    editionMode: Boolean,
    onAction: (ButtonAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.form_field_padding))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onAction(ButtonAction.CANCEL) }
        ) {
            Text(text = stringResource(R.string.screen_detail_btn_cancel))
        }

        if (editionMode) {
            UpdateButtons(onAction)
        } else {
            CreationButtons(onAction)
        }
    }
}

@Composable
private fun CreationButtons(
    onAction: (ButtonAction) -> Unit,
) {
    Button(
        onClick = { onAction(ButtonAction.CREATE) }
    ) {
        Text(text = stringResource(R.string.screen_detail_btn_create))
        Icon(
            Icons.Default.AddCircle,
            contentDescription = stringResource(R.string.screen_detail_btn_create)
        )
    }
}

@Composable
private fun UpdateButtons(
    onAction: (ButtonAction) -> Unit,
) {
    Button(
        onClick = { onAction(ButtonAction.DELETE) }
    ) {
        Text(text = stringResource(R.string.screen_detail_btn_delete))
        Icon(
            Icons.Default.Delete,
            contentDescription = stringResource(R.string.screen_detail_btn_delete)
        )
    }
    Button(
        onClick = { onAction(ButtonAction.CREATE) }
    ) {
        Text(text = stringResource(R.string.screen_detail_btn_save))
        Icon(
            Icons.Default.Edit,
            contentDescription = stringResource(R.string.screen_detail_btn_save)
        )
    }
}
