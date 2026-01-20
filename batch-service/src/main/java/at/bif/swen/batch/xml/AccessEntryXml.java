package at.bif.swen.batch.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class AccessEntryXml {

    @XmlElement(name = "documentId", required = true)
    private String documentId;

    @XmlElement(name = "count", required = true)
    private int count;
}
