## ActivityIntentCreater

## 该库过时

请使用`Activity Result API`, 旧版项目可以继续使用, 如果迁移了`androidx`, 建议使用`Activity Result API` 替代`startActivityForResult`;

多说一句: 不建议使用 网上那种 一行代码实现 启动actvity 并获取返回值的: 类似于我的 [StartActivityForResultHelper](https://github.com/yizems/StartActivityForResultHelper); 这种有个很明显的问题, 如果源activity 会回收,是接收不到回调的.

### 1 使用

![示例][1]

### 2 下载

**已发布到插件商店**

 下载主目录中的ActivityIntentCreater2.jar,然后在AS中选中本地安装,然后选中下载好的jar包即可
 ![AS](https://i.loli.net/2017/08/19/5998455f084d0.png)
 
### 3 注意事项

- 对于变量名修改后,修改生成的代码,或者直接重新生成
- 方法重载的问题需要手动处理一下


### 4 更新日志
- 1.4 支持Parcelable,Serializable序列化类型自动转换和使用,支持Parcelable的ArrayList的传递


  [1]: https://i.loli.net/2017/08/16/5993e049bb0af.jpg "1"
