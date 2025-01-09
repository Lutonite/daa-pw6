package ch.heigvd.iict.daa.rest.ui.composables

import androidx.annotation.DimenRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.sp

/**
 * Retrieve a [dimensionResource] and convert it to [sp] for font sizes
 *
 * @param id The dimension resource id
 * @return The dimension in [sp]
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
@Composable
@ReadOnlyComposable
fun fontDimensionResource(@DimenRes id: Int) = dimensionResource(id = id).value.sp