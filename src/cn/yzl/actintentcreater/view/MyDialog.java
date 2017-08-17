package cn.yzl.actintentcreater.view;

import cn.yzl.actintentcreater.modle.TypeBean;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDialog extends JDialog {
    private CallBack callback;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList jList;
    private JButton select_none;

    List<TypeBean> data;


    public MyDialog() {
        setTitle("选择需要传递的属性");
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        setModal(true);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        select_none.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNoneSelect();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


    }


    public MyDialog(List<TypeBean> data, CallBack callBack) {
        this();
        this.data = data;
        this.callback = callBack;
//        DefaultListModel model = new DefaultListModel();
        MyListModle model = new MyListModle(data);
//        for (TypeBean bean : data) {
//            model.addElement(bean.fieldName + ":" + bean.typeName);
//        }
        jList.setModel(model);
    }


    private void onOK() {
        Map<String, String> result = new HashMap<>();
        List<TypeBean> selectedValuesList = jList.getSelectedValuesList();
        dispose();
        callback.pick(selectedValuesList);
    }

    private void onNoneSelect() {
        callback.pick(new ArrayList<>());
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
//        Map<String, String> data = new HashMap<>();
//        data.put("111", "ss");
//        data.put("222", "sssss");
//        data.put("33344", "sss");
//        data.put("444", "sss");
//        MyDialog dialog = new MyDialog(types, new CallBack() {
//            @Override
//            public void pick(List<TypeBean> result) {
//                createCode(psiFile, psiClass, result);
//            }
//        });
//        dialog.pack();
//        dialog.setSize(500, 500);
//
//        Toolkit kit = Toolkit.getDefaultToolkit();    // 定义工具包
//
//        Dimension screenSize = kit.getScreenSize();   // 获取屏幕的尺寸
//
//        int screenWidth = screenSize.width / 2;         // 获取屏幕的宽
//
//        int screenHeight = screenSize.height / 2;       // 获取屏幕的高
//
//        int height = 500;
//
//        int width = 500;
//        dialog.setLocation(screenWidth - width / 2, screenHeight - height / 2);
//        dialog.setVisible(true);
//        System.exit(0);
    }

    public interface CallBack {
        void pick(List<TypeBean> result);
    }
}
