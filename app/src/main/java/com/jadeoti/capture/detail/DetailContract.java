package com.jadeoti.capture.detail;

import com.jadeoti.capture.data.model.Person;

/**
 * Created by Morph-Deji on 22-Mar-17.
 */

public interface DetailContract {
    interface View{
        void show(Person person);
    }

    interface ActionListener{

    }
}
