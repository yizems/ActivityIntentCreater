package cn.yzl.actintentcreater.utils;

import cn.yzl.actintentcreater.modle.TypeBean;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YZL on 2017/8/13.
 */
public class Utils {
    public static Map<String, String> INTENT_METHODS;

    private static Map<String, String> getMethods() {
        if (INTENT_METHODS != null) {
            return INTENT_METHODS;
        }
        INTENT_METHODS = new HashMap<>();
        INTENT_METHODS.put("int", ".getIntExtra(\"#\",0)");
        INTENT_METHODS.put("int[]", ".getIntArrayExtra(\"#\")");
        INTENT_METHODS.put("Integer", ".getIntExtra(\"#\",0)");
        INTENT_METHODS.put("Integer[]", ".getIntArrayExtra(\"#\")");
        INTENT_METHODS.put("String", ".getStringExtra(\"#\")");
        INTENT_METHODS.put("boolean", ".getBooleanExtra(\"#\",false)");
        INTENT_METHODS.put("boolean[]", ".getBooleanArrayExtra(\"#\")");
        INTENT_METHODS.put("Boolean", ".getBooleanExtra(\"#\",false)");
        INTENT_METHODS.put("Boolean[]", ".getBooleanArrayExtra(\"#\")");
        INTENT_METHODS.put("Bundle", ".getBundleExtra(\"#\")");
        INTENT_METHODS.put("byte", ".getByteExtra(\"#\",(byte) 0)");
        INTENT_METHODS.put("byte[]", ".getByteArrayExtra(\"#\")");
        INTENT_METHODS.put("Byte", ".getByteExtra(\"#\",(byte) 0)");
        INTENT_METHODS.put("Byte[]", ".getByteArrayExtra(\"#\")");
        INTENT_METHODS.put("char", ".getCharExtra(\"#\",'0')");
        INTENT_METHODS.put("char[]", ".getCharArrayExtra(\"#\")");
        INTENT_METHODS.put("Char", ".getCharExtra(\"#\",'0')");
        INTENT_METHODS.put("Char[]", ".getCharArrayExtra(\"#\")");
        INTENT_METHODS.put("double", ".getDoubleExtra(\"#\",0d)");
        INTENT_METHODS.put("double[]", ".getDoubleArrayExtra(\"#\")");
        INTENT_METHODS.put("Double", ".getDoubleExtra(\"#\",0d)");
        INTENT_METHODS.put("Double[]", ".getDoubleArrayExtra(\"#\")");

        INTENT_METHODS.put("float", ".getFloatExtra(\"#\",0f)");
        INTENT_METHODS.put("float[]", ".getFloatArrayExtra(\"#\")");
        INTENT_METHODS.put("Float", ".getFloatExtra(\"#\",0f)");
        INTENT_METHODS.put("Float[]", ".getFloatArrayExtra(\"#\")");
        INTENT_METHODS.put("long", ".getLongExtra(\"#\",0l)");
        INTENT_METHODS.put("long[]", ".getLongArrayExtra(\"#\")");
        INTENT_METHODS.put("Long", ".getLongExtra(\"#\",0l)");
        INTENT_METHODS.put("Long[]", ".getLongArrayExtra(\"#\")");
        INTENT_METHODS.put("CharSequence", ".getCharSequenceExtra(\"#\")");
        INTENT_METHODS.put("CharSequence[]", ".getCharSequenceArrayExtra(\"#\")");
        INTENT_METHODS.put("CharSequence[]", ".getCharSequenceArrayExtra(\"#\")");
        INTENT_METHODS.put("ArrayList<String>", ".getStringArrayListExtra(\"#\")");
        INTENT_METHODS.put("ArrayList<Integer>", ".getIntegerArrayListExtra(\"#\")");
        INTENT_METHODS.put("ArrayList<CharSequence>", ".getCharSequenceArrayListExtra(\"#\")");
        return INTENT_METHODS;
    }

    /**
     * 预处理 type
     *
     * @param typeBean
     * @return true 受支持的数据类型,false,不受支持
     */
    public static boolean prepareType(TypeBean typeBean) {
        //数组,list,普通 标记
        if (typeBean.typeName.contains("[]")) {
            typeBean.arrayType = TypeBean.TYPE_ARRAY;
        } else if (typeBean.typeName.startsWith("ArrayList")) {
            typeBean.arrayType = TypeBean.TYPE_LIST;
        } else {
            typeBean.arrayType = TypeBean.TYPE_NORMAL;
        }
        if (typeBean.arrayType == TypeBean.TYPE_LIST) {
            //List的处理
            return isSupportList(typeBean);
        } else {
            if (isJavaBaseType(typeBean)) {
                return true;
            }
            if (typeBean.arrayType == TypeBean.TYPE_ARRAY) {
                if (typeBean.psiType instanceof PsiArrayType) {
                    typeBean.psiType = ((PsiArrayType) typeBean.psiType).getComponentType();
                }
            }
            PsiClass psiClass = PsiTypesUtil.getPsiClass(typeBean.psiType);
            String qualifiedName = psiClass.getQualifiedName();
            if (qualifiedName.startsWith("android.")
                    && (!qualifiedName.equals("android.os.Parcelable"))) {
                return false;
            }
            int result = filteType(psiClass);
            if (result == 1) {
                typeBean.isParcelable = true;
                return true;
            } else if (result == 2) {
                typeBean.isSerializable = true;
                if (typeBean.arrayType == TypeBean.TYPE_ARRAY) {
                    //不支持 Serializable 数组传递
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 是否是支持的ArrayList,并获得泛型的名字
     *
     * @param typeBean
     * @return
     */
    private static boolean isSupportList(TypeBean typeBean) {
        if (typeBean.typeName.equals("ArrayList<String>")
                || typeBean.typeName.equals("ArrayList<Integer>")
                || typeBean.typeName.equals("ArrayList<CharSequence>")) {
            typeBean.genericsTypeName = typeBean.typeName.replace("ArrayList<", "")
                    .replace(">", "");
            return true;
        }
        PsiType psiType = typeBean.psiType;
        //查找泛型 是否受支持
        if (psiType instanceof PsiClassType) {
            PsiType[] parameters = ((PsiClassType) psiType).getParameters();
            if (parameters != null
                    && parameters.length > 0) {
                PsiType genericType = parameters[0];
                if (filteType(PsiTypesUtil.getPsiClass(genericType)) == 1) {
                    typeBean.isParcelable = true;
                    typeBean.arrayType = TypeBean.TYPE_LIST;
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * @param psiClass
     * @return 0 不支持,1 Parcelable,2,Serializable
     */
    public static int filteType(PsiClass psiClass) {
        if (psiClass == null) {
            return 0;
        }
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName.equals("android.os.Parcelable")) {
            return 1;
        }
        if (qualifiedName.equals("java.io.Serializable")) {
            return 2;
        }

        PsiClass[] supers = psiClass.getSupers();
        if (supers == null) {
            return 0;
        }
        for (int i = 0; i < supers.length; i++) {
            int result = filteType(supers[i]);
            if (result > 0) {
                return result;
            }
        }
        return 0;
    }


    public static boolean isJavaBaseType(TypeBean typeBean) {
        String type = typeBean.typeName;
        if (type.contains("[]")) {
            type = type.replace("[]", "");
        }
        if (type.equals("int")
                || type.equals("Integer")
                || type.equals("String")
                || type.equals("boolean")
                || type.equals("Boolean")
                || type.equals("Bundle")
                || type.equals("byte")
                || type.equals("Byte")
                || type.equals("char")
                || type.equals("Char")
                || type.equals("double")
                || type.equals("Double")
                || type.equals("float")
                || type.equals("Float")
                || type.equals("long")
                || type.equals("Long")
                || type.equals("CharSequence")
                ) {
            return true;
        }
        return false;
    }

    /**
     * @param typeBean
     * @return .getIntExtra()
     */
    public static String generateGetMethod(TypeBean typeBean) {
        StringBuffer sb = new StringBuffer();
        sb.append(typeBean.fieldName + "=intent");
        if (typeBean.arrayType == TypeBean.TYPE_NORMAL) {
            if (typeBean.isSerializable) {
                sb.append(".getSerializableExtra(\"#\")".replace("#", typeBean.fieldName) + ";");
                return sb.toString().replace("=", "=(" + typeBean.typeName + ")");
            }
            if (typeBean.isParcelable) {
                sb.append(".getParcelableExtra(\"#\")".replace("#", typeBean.fieldName) + ";");
                return sb.toString();
            }
            if (getMethods().get(typeBean.typeName) == null) {
                return "";
            }
            sb.append(getMethods().get(typeBean.typeName).replace("#", typeBean.fieldName) + ";");
            return sb.toString();
        } else if (typeBean.arrayType == TypeBean.TYPE_LIST) {
            if (typeBean.isParcelable) {
                sb.append(".getParcelableArrayListExtra(\"#\")".replace("#", typeBean.fieldName)+";");
                return sb.toString();
            }
            if (getMethods().get(typeBean.typeName) == null) {
                return "";
            }
            sb.append(getMethods().get(typeBean.typeName).replace("#", typeBean.fieldName) + ";");

            return sb.toString();
        } else if (typeBean.arrayType == TypeBean.TYPE_ARRAY) {
            if (typeBean.isParcelable) {
                sb.append(".getParcelableArrayExtra(\"#\")".replace("#", typeBean.fieldName) + ";");
                return sb.toString().replace("=", "=(" + typeBean.typeName + ")");
            }
            if (getMethods().get(typeBean.typeName) == null) {
                return "";
            }
            sb.append(getMethods().get(typeBean.typeName).replace("#", typeBean.fieldName) + ";");
            return sb.toString();
        } else {
            return "";
        }
    }
}
