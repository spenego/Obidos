/*
 * Copyright (C) 2016 - 2026 Spenego Software LLC. All rights reserved.
 *
 * This file is part of Obidos from Spenego Software LLC
 *
 * Obidos is dual-licensed under a commercial license and the GNU
 * Affero General Public License (AGPL) v3.0. For commercial licensing,
 * contact Spenego Software LLC at https://spenego.com/contacts.html.
 *
 * For AGPL licensing terms, see the LICENSE file in the project root
 * or <https://www.gnu.org/licenses/>.
 */

package com.spenego.Obidos.client.application.uploadprofilepic;

import com.google.gwt.event.dom.client.ClickEvent;
import com.gwtplatform.mvp.client.UiHandlers;

interface UploadProfilePicUiHandlers extends UiHandlers
{
	void help();
	void back();
	void priviewImageClickHandler(ClickEvent e);
	void readUploadingImageFile();
	void uploadImage(boolean goback);
	void cropImage();
	void downloadImage();
	void downloadRegion();
	void annotateImage();
	void grayScaleImage();
	void sepiaImage();
	void noiseImage();
	void brightenImage();
	void darkenImage();
	void sharpenImage();
	void negateImage();
	void solarizeImage();
	void histogramImage();
	void redImage();
	void greenImage();
	void blueImage();
	void revertFilters();
}
