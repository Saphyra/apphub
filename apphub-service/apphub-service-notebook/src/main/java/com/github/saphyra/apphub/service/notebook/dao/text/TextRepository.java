package com.github.saphyra.apphub.service.notebook.dao.text;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface TextRepository extends CrudRepository<TextEntity, String> {
    void deleteByParent(String parent);
}
