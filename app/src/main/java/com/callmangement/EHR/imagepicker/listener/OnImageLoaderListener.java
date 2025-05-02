package com.callmangement.EHR.imagepicker.listener;



import com.callmangement.EHR.imagepicker.model.Folder;
import com.callmangement.EHR.imagepicker.model.Image;

import java.util.List;

/**
 * Created by hoanglam on 8/17/17.
 */

public interface OnImageLoaderListener {
    void onImageLoaded(List<Image> images, List<Folder> folders);

    void onFailed(Throwable throwable);
}
