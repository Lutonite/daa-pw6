package ch.heigvd.iict.daa.rest.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import ch.heigvd.iict.daa.rest.R

enum class PhoneType {
    HOME, OFFICE, MOBILE, FAX
}

@Composable
@ReadOnlyComposable
fun PhoneType.description(): String {
    return when (this) {
        PhoneType.HOME -> stringResource(id = R.string.phonetype_home)
        PhoneType.OFFICE -> stringResource(id = R.string.phonetype_office)
        PhoneType.MOBILE -> stringResource(id = R.string.phonetype_mobile)
        PhoneType.FAX -> stringResource(id = R.string.phonetype_fax)
    }
}
