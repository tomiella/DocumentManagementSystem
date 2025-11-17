package at.bif.swen.rest.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StoragePort {

    String store(MultipartFile file) throws IOException;

    Resource loadAsResource(String key) throws IOException;
}