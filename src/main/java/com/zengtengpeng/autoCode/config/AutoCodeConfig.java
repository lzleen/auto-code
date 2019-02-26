package com.zengtengpeng.autoCode.config;


import com.zengtengpeng.autoCode.create.*;
import com.zengtengpeng.jdbc.bean.Bean;

/**
 * 全局配置
 */
public class AutoCodeConfig {
    //数据库配置
    private DatasourceConfig datasourceConfig;

    //全局配置
    private GlobalConfig globalConfig;

    private Bean bean;


    private BuildDao buildDao=t->null;
    private BuildService buildService=t->null;
    private BuildServiceImpl buildServiceImpl= t->null;
    private BuildController buildController= t->null;
    private BuildBean buildBean= t->null;

    private BuildXml buildXml=t->null;

    public BuildService getBuildService() {
        return buildService;
    }

    public BuildServiceImpl getBuildServiceImpl() {
        return buildServiceImpl;
    }

    public BuildController getBuildController() {
        return buildController;
    }

    public BuildBean getBuildBean() {
        return buildBean;
    }

    public void setBuildBean(BuildBean buildBean) {
        this.buildBean = buildBean;
    }

    public void setBuildController(BuildController buildController) {
        this.buildController = buildController;
    }

    public void setBuildServiceImpl(BuildServiceImpl buildServiceImpl) {
        this.buildServiceImpl = buildServiceImpl;
    }

    public void setBuildService(BuildService buildService) {
        this.buildService = buildService;
    }

    public BuildDao getBuildDao() {
        return buildDao;
    }

    public void setBuildDao(BuildDao buildDao) {
        this.buildDao = buildDao;
    }

    public BuildXml getBuildXml() {
        return buildXml;
    }

    public void setBuildXml(BuildXml buildXml) {
        this.buildXml = buildXml;
    }

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public DatasourceConfig getDatasourceConfig() {
        return datasourceConfig;
    }

    public void setDatasourceConfig(DatasourceConfig datasourceConfig) {
        this.datasourceConfig = datasourceConfig;
    }


}