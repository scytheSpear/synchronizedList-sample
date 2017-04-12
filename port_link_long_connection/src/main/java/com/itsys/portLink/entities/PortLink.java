package com.itsys.portLink.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortLink implements Serializable {

    private String sourceIp;
    private String targetIp;
    private int sourcePort;
    private int targetPort;
    private String protocol;
    private Date startTime;
    private Date endTime;
    private String remark;
    private String id;
}
