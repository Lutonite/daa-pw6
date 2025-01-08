package ch.heigvd.iict.daa.rest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.iict.daa.rest.R
import ch.heigvd.iict.daa.rest.models.Contact
import ch.heigvd.iict.daa.rest.models.PhoneType

@Composable
fun ScreenContactList(contacts: List<Contact>, onContactSelected: (Contact) -> Unit) {
    Column {
        Text(text = stringResource(R.string.screen_list_title), fontSize = 24.sp)
        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.screen_list_empty),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(contacts) { item ->
                    ContactItemView(item) { clickedContact ->
                        onContactSelected(clickedContact)
                    }
                }
            }

        }
    }
}

@Composable
fun ContactItemView(contact: Contact, onClick: (Contact) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(2.dp)
            .clickable {
                onClick(contact)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.contact),
            contentDescription = stringResource(id = R.string.screen_list_contacticon_ctndesc),
            colorFilter = ColorFilter.tint(if (contact.synced) Color.Green else Color.Magenta),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "${if (contact.firstname == null) "" else contact.firstname} ${contact.name}".trim())
            Text(text = "${contact.phoneNumber}")
        }
        Image(
            painter = painterResource(
                when (contact.type) {
                    PhoneType.MOBILE -> R.drawable.cellphone
                    PhoneType.FAX -> R.drawable.fax
                    PhoneType.HOME -> R.drawable.phone
                    PhoneType.OFFICE -> R.drawable.office
                    else -> R.drawable.office
                }
            ),
            contentDescription = stringResource(id = R.string.screen_list_contacttype_ctndesc),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
    }
}
