package com.kidsdynamic.data.net.firmware.model;

/**
 * Created by Administrator on 2017/10/21.
 */

public class FirmwareVersionEntity {

    /**
     * id : 1
     * version : A2
     * fileAUrl : fw_version/A2A.hex
     * fileBUrl : fw_version/A2B.hex
     * uploadedDate : 2017-07-08T23:21:21Z
     */

    private int id;
    private String version;
    private String fileAUrl;
    private String fileBUrl;
    private String uploadedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFileAUrl() {
        return fileAUrl;
    }

    public void setFileAUrl(String fileAUrl) {
        this.fileAUrl = fileAUrl;
    }

    public String getFileBUrl() {
        return fileBUrl;
    }

    public void setFileBUrl(String fileBUrl) {
        this.fileBUrl = fileBUrl;
    }

    public String getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(String uploadedDate) {
        this.uploadedDate = uploadedDate;
    }
}
