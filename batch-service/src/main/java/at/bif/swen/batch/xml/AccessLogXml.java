package at.bif.swen.batch.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name = "accessLog")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class AccessLogXml {

    @XmlAttribute(name = "date", required = true)
    private String date;

    @XmlElement(name = "access")
    private List<AccessEntryXml> entries;
}
