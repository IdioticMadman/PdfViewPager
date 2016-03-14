/*
 * Copyright (C) 2016 Olmo Gallegos Hernández.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.voghdev.pdfviewpager.library.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import es.voghdev.pdfviewpager.library.R;
import es.voghdev.pdfviewpager.library.view.Zoomable;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PDFPagerAdapterZoom extends PDFPagerAdapter implements Zoomable{
    protected static final float NO_SCALE = 0f;

    float scale = NO_SCALE;
    SparseArray<WeakReference<PhotoViewAttacher>> attachers;

    public PDFPagerAdapterZoom(Context context, String pdfPath) {
        super(context, pdfPath);
        attachers = new SparseArray<>();
    }

    @Override
    @SuppressWarnings("NewApi")
    public Object instantiateItem(ViewGroup container, int position) {
        View v = inflater.inflate(R.layout.view_pdf_page, container, false);
        ImageView iv = (ImageView) v.findViewById(R.id.imageView);

        if(renderer == null || getCount() < position)
            return v;

        PdfRenderer.Page page = getPDFPage(position);

        Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),
                Bitmap.Config.ARGB_8888);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();

        PhotoViewAttacher attacher = new PhotoViewAttacher(iv);

        bitmaps.put(position, new WeakReference<Bitmap>(bitmap));
        attachers.put(position, new WeakReference<PhotoViewAttacher>(attacher));
        iv.setImageBitmap(bitmap);

        if(scale != NO_SCALE)
            attacher.setScale(scale);

        attacher.update();
        ((ViewPager) container).addView(v, 0);

        return v;
    }

    @Override
    public void close() {
        super.close();
        if(attachers != null){
            attachers.clear();
            attachers = null;
        }
    }

    @Override
    public void zoomTo(float scale) {
        this.scale = scale;
        notifyDataSetChanged();
    }
}
