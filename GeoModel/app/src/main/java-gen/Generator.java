
import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Created by king on 2016/9/25.
 */
public class Generator {
    public static void main(String[] args) throws Exception {

        int version = 1;
        String defaultPackage = "com.king.greenDAO.bean";
        //创建模式对象，指定版本号和自动生成的bean对象的包名
        Schema schema = new Schema(version, defaultPackage);
        //指定自动生成的dao对象的包名,不指定则都DAO类生成在"test.greenDAO.bean"包中
        schema.setDefaultJavaPackageDao("com.king.greenDAO.dao");

        //添加实体
        addEntity(schema);

        String outDir = "G:/train/GeoModel/app/src/main/java-gen";
        //调用DaoGenerator().generateAll方法自动生成代码到之前创建的java-gen目录下
        new DaoGenerator().generateAll(schema, outDir);

    }

    private static void addEntity(Schema schema) {
        //添加一个实体，则会自动生成实体Entity类
        //用户表
        Entity user = schema.addEntity("User");
        //指定表名，如不指定，表名则为 Entity（即实体类名）
        user.implementsSerializable();
        user.addIdProperty();
        user.addStringProperty("phone");
        user.addStringProperty("password");

        //数据表
        Entity model = schema.addEntity("GeoModel");
        //指定表名，如不指定，表名则为 Entity（即实体类名）
        model.implementsSerializable();
        //给实体类中添加属性（即给test表中添加字段）
        model.addIdProperty();
        model.addStringProperty("time");
        model.addStringProperty("title");
        model.addStringProperty("des");
        model.addStringProperty("status");
        model.addStringProperty("photos");//图片地址(对应本地sb卡地址，以","分隔)

        //位置信息表
        Entity position = schema.addEntity("PositionEntity");
        position.implementsSerializable();
        position.addIdProperty();
        position.addDoubleProperty("longitude");
        position.addDoubleProperty("latitude");
        position.addStringProperty("address");
        position.addStringProperty("country");
        position.addStringProperty("province");
        position.addStringProperty("city");
        position.addStringProperty("district");
        position.addStringProperty("street");
        position.addStringProperty("time");


        //为数据表添加一个userId外键，它就是user表的id
        Property userId = model.addLongProperty("userId").getProperty();
        //这里是重点，我们为这两个表建立1:n的关系，并设置关联字段。
        model.addToOne(user, userId);
        ToMany addToMany = user.addToMany(model, userId);
        addToMany.setName("models");

        //这里我们为Position添加一个modelId外键，它就是model表的id
        Property modelId = position.addLongProperty("modelId").getProperty();
        position.addToOne(model, modelId);
        ToMany addToMany2 = model.addToMany(position, modelId);
        addToMany2.setName("positions");

    }
}