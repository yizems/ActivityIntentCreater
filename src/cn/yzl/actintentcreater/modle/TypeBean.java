package cn.yzl.actintentcreater.modle;

import com.intellij.psi.PsiType;

/**
 * Created by YZL on 2017/8/15.
 */
public class TypeBean {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_ARRAY = 1;
    public static final int TYPE_LIST = 2;

    public String fieldName;


    public String typeName;


    public PsiType psiType;


    public boolean isSerializable;


    public boolean isParcelable;

    /**
     * 0:不是
     * 1:数组
     * 3:ArrayList
     */
    public int arrayType;

    public String genericsTypeName;

    @Override
    public String toString() {
        return fieldName + ":" + typeName;
    }

}
