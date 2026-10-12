/*
 * Copyright 2024 onseok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.lamplis.vision.gallery.repository

import android.content.Context
import androidx.paging.PagingSource
import io.github.lamplis.vision.gallery.datasource.VisionGalleryDataSource
import io.github.lamplis.vision.gallery.model.VisionMediaImage
import io.github.lamplis.vision.gallery.util.createCursor
import io.github.lamplis.vision.gallery.util.fetchPagePicture

internal class VisionGalleryRepositoryImpl(
    private val context: Context,
) : VisionGalleryRepository {
    override suspend fun getCount(): Int {
        val cursor = context.createCursor(Int.MAX_VALUE, 0) ?: return 0
        val count = cursor.count
        cursor.close()
        return count
    }

    override suspend fun getByOffset(offset: Int): VisionMediaImage? = context.fetchPagePicture(1, offset).firstOrNull()

    override fun getPicturePagingSource(): PagingSource<Int, VisionMediaImage> =
        VisionGalleryDataSource { limit, offset -> context.fetchPagePicture(limit, offset) }
}
