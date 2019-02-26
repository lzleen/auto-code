package com.zengtengpeng.autoCode.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zengtengpeng.autoCode.bean.BuildJavaField;
import com.zengtengpeng.autoCode.bean.BuildJavaMethod;
import com.zengtengpeng.autoCode.config.AutoCodeConfig;
import com.zengtengpeng.autoCode.config.BuildJavaConfig;
import com.zengtengpeng.autoCode.config.GlobalConfig;
import com.zengtengpeng.autoCode.utils.BuildUtils;
import com.zengtengpeng.autoCode.utils.MyStringUtils;
import com.zengtengpeng.jdbc.bean.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生成bean
 */
@FunctionalInterface
public interface BuildBean {

    StringBuffer stringBuffer = new StringBuffer();

    Logger logger = LoggerFactory.getLogger(BuildBean.class);

    default BuildBean before(AutoCodeConfig autoCodeConfig, BuildJavaConfig buildJavaConfig) {
        GlobalConfig globalConfig = autoCodeConfig.getGlobalConfig();
        MyStringUtils.append(stringBuffer, "package %s.%s;", globalConfig.getParentPack(), globalConfig.getPackageBean());
        return this;
    }

    /**
     * 构建导入包
     *
     * @param autoCodeConfig
     * @return
     */
    default BuildBean buildImports(AutoCodeConfig autoCodeConfig, BuildJavaConfig buildJavaConfig) {
        if (buildJavaConfig == null) {
            buildJavaConfig = new BuildJavaConfig();
        }
        GlobalConfig globalConfig = autoCodeConfig.getGlobalConfig();
        Bean bean = autoCodeConfig.getBean();
        List<String> imports = buildJavaConfig.getImports();
        if (imports == null) {
            imports = new ArrayList<>();
        }
        if (buildJavaConfig.getDefaultRealize()) {
            imports.add("com.fasterxml.jackson.annotation.JsonIgnore");
            imports.add("com.zengtengpeng.common.bean.Pag");
            imports.add("com.zengtengpeng.common.utils.DateUtils");
            imports.add("java.util.Date");
            imports.add("java.math.BigDecimal");
            imports.add("com.zengtengpeng.autoCode.utils.MyStringUtils");
        }
        imports.forEach(t -> stringBuffer.append("import " + t + ";\n"));

        stringBuffer.append("\n\n");
        return this;
    }

    /**
     * 构建class
     *
     * @param autoCodeConfig
     * @return
     */
    default BuildBean buildClass(AutoCodeConfig autoCodeConfig, BuildJavaConfig buildJavaConfig) {
        if (buildJavaConfig == null) {
            buildJavaConfig = new BuildJavaConfig();
        }
        Bean bean = autoCodeConfig.getBean();
        GlobalConfig globalConfig = autoCodeConfig.getGlobalConfig();
        StringBuffer extendsb = new StringBuffer();
        StringBuffer isb = new StringBuffer();
        List<String> extend = buildJavaConfig.getExtend();
        if (extend == null) {
            extend = new ArrayList<>();
        }
        if(MyStringUtils.isEmpty(buildJavaConfig.getRemark())){
            buildJavaConfig.setRemark(bean.getTableRemarks());
        }
        stringBuffer.append("/**\n" +
                " *" +buildJavaConfig.getRemark()+" bean"+
                "\n */\n");

        List<String> implement = buildJavaConfig.getImplement();

        if (implement == null) {
            implement = new ArrayList<>();
        }

        List<String> annotations = buildJavaConfig.getAnnotations();
        if (annotations == null) {
            annotations = new ArrayList<>();
        }

        if (buildJavaConfig.getDefaultRealize()) {
            extend.add("Page");
        }
        extend.forEach(t -> extendsb.append(t + ","));

        implement.forEach(t -> isb.append(t + ","));

        String s1 = "";
        if (extendsb.length() > 0) {
            s1 = "extends " + extendsb.substring(0, extendsb.length() - 1);
        }

        String s2 = "";
        if (isb.length() > 0) {
            s2 = "implements " + isb.substring(0, isb.length() - 1);
        }


        annotations.forEach(t -> stringBuffer.append(t + "\n"));
        MyStringUtils.append(stringBuffer, "public class %s  %s %s {\n\n",
                bean.getTableName(),  s1, s2);
        return this;
    }

    /**
     * 构建字段
     *
     * @param autoCodeConfig
     * @return
     */
    default BuildBean buildField(AutoCodeConfig autoCodeConfig, BuildJavaConfig buildJavaConfig) {
        if (buildJavaConfig == null) {
            buildJavaConfig = new BuildJavaConfig();
        }
        List<BuildJavaField> buildJavaFields = buildJavaConfig.getBuildJavaFields();
        if (buildJavaFields == null) {
            buildJavaFields = new ArrayList<>();
        }
        if (buildJavaConfig.getDefaultRealize()) {
            Bean bean = autoCodeConfig.getBean();
            List<BuildJavaField> finalBuildJavaFields = buildJavaFields;
            bean.getAllColumns().forEach(t->{
                BuildJavaField buildJavaField = new BuildJavaField();
                buildJavaField.setReturnType(t.getBeanType_());
                buildJavaField.setFiledType("private");
                buildJavaField.setFiledName(t.getBeanName());
                buildJavaField.setRemark(t.getRemarks());
                finalBuildJavaFields.add(buildJavaField);
            });

            buildJavaConfig.setBuildJavaFields(finalBuildJavaFields);
        }
        BuildUtils.buildField(buildJavaConfig, stringBuffer);
        return this;
    }

    /**
     * 构建方法
     *
     * @param autoCodeConfig
     * @return
     */
    default BuildBean buildMethods(AutoCodeConfig autoCodeConfig, BuildJavaConfig buildJavaConfig) {
        if (buildJavaConfig == null) {
            buildJavaConfig = new BuildJavaConfig();
        }
        List<BuildJavaMethod> buildJavaMethods = buildJavaConfig.getBuildJavaMethods();
        if (buildJavaMethods == null) {
            buildJavaMethods = new ArrayList<>();
        }

        if (buildJavaConfig.getDefaultRealize()) {
            Bean bean = autoCodeConfig.getBean();
            List<BuildJavaMethod> finalBuildJavaMethods = buildJavaMethods;
            bean.getAllColumns().forEach(t->{
                //get
                BuildJavaMethod get = new BuildJavaMethod();

                //如果是date则坐下处理
                if("Date".equals(t.getBeanType_())){
                    List<String> an=new ArrayList<>();
                    an.add("@JsonIgnore");
                    get.setAnnotation(an);

                    BuildJavaMethod get1 = new BuildJavaMethod();
                    get1.setReturnType(t.getBeanType_());
                    get1.setMethodType("public");
                    get1.setMethodName("get"+t.getBeanName_()+"_");
                    if("DATE".equals(t.getJdbcType_())){
                        get1.setContent("return DateUtils.formatDate(" +t.getBeanName()+")");
                    }else {
                        get1.setContent("return DateUtils.formatDateTime(" +t.getBeanName()+")");
                    }
                    finalBuildJavaMethods.add(get1);
                }

                //如果注释是json格式的则转成key value
                //转换json
                ObjectMapper objectMapper=new ObjectMapper();
                String remarks = t.getRemarks();
                try {
                    Map<Object,Object> map = objectMapper.readValue(remarks, Map.class);
                    List<String> an=new ArrayList<>();
                    an.add("@JsonIgnore");
                    get.setAnnotation(an);

                    BuildJavaMethod get1 = new BuildJavaMethod();
                    get1.setReturnType(t.getBeanType_());
                    get1.setMethodType("public");
                    get1.setMethodName("get"+t.getBeanName_()+"_");
                    StringBuffer json=new StringBuffer();
                    json.append("if(MyStringUtils.isEmpty(status)){\n");
                    MyStringUtils.append(json," return \"\";",3);

                    for (Map.Entry me : map.entrySet()) {
                        if("name".equals(me.getKey())){
                            continue;
                        }
                        MyStringUtils.append(json,"}else if(%s.equals(\"%s\")){",2,t.getBeanName(),me.getKey().toString());
                        MyStringUtils.append(json,"\"%s\";",3,me.getValue().toString());
                    }
                    MyStringUtils.append(json,"}",2);
                    MyStringUtils.append(json,"return \"\";",2);
                    get1.setContent(json.toString());
                    finalBuildJavaMethods.add(get1);
                } catch (Exception e) {
                    logger.info("字段->"+t.getBeanName()+"注释->"+t.getRemarks()+"不是json忽略转换");
                    System.out.printf("");
                }

                get.setReturnType(t.getBeanType_());
                get.setMethodType("public");
                get.setMethodName("get"+t.getBeanName_());
                get.setContent("return " +t.getBeanName()+";");
                finalBuildJavaMethods.add(get);

                //set
                BuildJavaMethod set = new BuildJavaMethod();
                set.setReturnType("void");
                set.setMethodType("public");
                set.setMethodName("set"+t.getBeanName_());
                List<String> params=new ArrayList<>();
                params.add(String.format("%s %s",t.getBeanType_(),t.getBeanName()));
                set.setParams(params);
                set.setContent(String.format("this.%s=%s;",t.getBeanName(),t.getBeanName()));
                finalBuildJavaMethods.add(set);
            });

            buildJavaConfig.setBuildJavaMethods(finalBuildJavaMethods);
        }
        BuildUtils.buildMethods(buildJavaConfig, stringBuffer);
        return this;
    }


    /**
     * 结束
     *
     * @param autoCodeConfig
     * @return
     */
    default BuildBean end(AutoCodeConfig autoCodeConfig, BuildJavaConfig buildJavaConfig) {
        stringBuffer.append("}\n");
        return this;
    }

    /**
     * 自定义方法
     *
     * @param
     * @return
     */
    BuildJavaConfig custom(AutoCodeConfig autoCodeConfig);

    /**
     * 构建serviceImpl
     *
     * @param autoCodeConfig
     * @return
     */
    default String build(AutoCodeConfig autoCodeConfig) {
        BuildJavaConfig buildJavaConfig = custom(autoCodeConfig);
        before(autoCodeConfig, buildJavaConfig).
                buildImports(autoCodeConfig, buildJavaConfig)
                .buildClass(autoCodeConfig, buildJavaConfig)
                .buildField(autoCodeConfig, buildJavaConfig)
                .buildMethods(autoCodeConfig, buildJavaConfig)
                .end(autoCodeConfig, buildJavaConfig);

        return stringBuffer.toString();
    }


}