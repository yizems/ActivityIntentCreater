package cn.yzl.actintentcreater.view;

import cn.yzl.actintentcreater.modle.TypeBean;

import javax.swing.*;
import java.util.List;

/**
 * Created by YZL on 2017/8/15.
 */
public class MyListModle extends DefaultListModel<TypeBean> {
    List<TypeBean> data;

    public MyListModle(List<TypeBean> bean) {
        this.data = bean;
        for (int i = 0; i < data.size(); i++) {
            addElement(data.get(i));
        }
    }

    @Override
    public int getSize() {
        return data.size();
    }
}
