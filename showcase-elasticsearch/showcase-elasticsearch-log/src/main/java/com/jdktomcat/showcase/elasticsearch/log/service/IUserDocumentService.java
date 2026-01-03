package com.jdktomcat.showcase.elasticsearch.base.service;

import com.jdktomcat.showcase.elasticsearch.base.entry.UserDocument;

import java.io.IOException;
import java.util.List;

public interface IUserDocumentService {

    boolean createUserIndex(String index) throws IOException;

    Boolean deleteUserIndex(String index) throws IOException ;

    Boolean createUserDocument(UserDocument document, String index) throws Exception;

    Boolean bulkCreateUserDocument(List<UserDocument> documents, String index) throws IOException;

    UserDocument getUserDocument(String id, String index) throws IOException;
}
