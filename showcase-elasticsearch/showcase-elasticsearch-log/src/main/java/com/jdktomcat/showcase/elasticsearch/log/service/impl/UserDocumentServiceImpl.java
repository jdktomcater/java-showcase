package com.jdktomcat.showcase.elasticsearch.log.service.impl;

import com.jdktomcat.showcase.elasticsearch.log.dao.UserDocumentEsDAO;
import com.jdktomcat.showcase.elasticsearch.log.entry.UserDocument;
import com.jdktomcat.showcase.elasticsearch.log.service.IUserDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class UserDocumentServiceImpl implements IUserDocumentService {

    @Autowired
    private UserDocumentEsDAO userDocumentEsDAO;


    @Override
    public boolean createUserIndex(String index) throws IOException {
        return userDocumentEsDAO.createUserIndex(index);
    }

    @Override
    public Boolean deleteUserIndex(String index) throws IOException {
        return userDocumentEsDAO.deleteUserIndex(index);
    }

    @Override
    public Boolean createUserDocument(UserDocument document, String index) throws Exception {
        return userDocumentEsDAO.createUserDocument(document,index);
    }

    @Override
    public Boolean bulkCreateUserDocument(List<UserDocument> documents, String index) throws IOException {
        return userDocumentEsDAO.bulkCreateUserDocument(documents, index);
    }

    @Override
    public UserDocument getUserDocument(String id, String index) throws IOException {
        return userDocumentEsDAO.getUserDocument(id, index);
    }
}
