(function SqlGenerator(){
    const TYPE_TITLE = "TITLE";
    const TYPE_DETAIL = "DESCRIPTION";
    const LOCALE_EN = "en";
    const LOCALE_DE = "de";

    window.sqlGenerator = new function(){
        this.generate = generate;
    }

    function generate(){
        const result = document.getElementById(ids.resultContainer);
            result.innerHTML = "";

        const messageType = document.getElementById(ids.messageTypeInput).value;
        const os = document.getElementById(ids.osInput).value;
        const version = document.getElementById(ids.version).value;
        const showContact = document.getElementById(ids.showContact).checked;

        const enTitle = document.getElementById(ids.enTitleInput).value;
        const enDetail = document.getElementById(ids.enDetailInput).value;
        const deTitle = document.getElementById(ids.deTitleInput).value;
        const deDetail = document.getElementById(ids.deDetailInput).value;

        const sqls = [];

        sqls.push(generateMobileMessageSql(messageType, os, version, showContact));
        sqls.push(generateLocalizedTextSql(messageType, TYPE_TITLE, os, LOCALE_EN, enTitle));
        sqls.push(generateLocalizedTextSql(messageType, TYPE_DETAIL, os, LOCALE_EN, enDetail));
        sqls.push(generateLocalizedTextSql(messageType, TYPE_TITLE, os, LOCALE_DE, deTitle));
        sqls.push(generateLocalizedTextSql(messageType, TYPE_DETAIL, os, LOCALE_DE, deDetail));

        new Stream(sqls)
            .map(escapeTags)
            .map(createNode)
            .forEach(function(node){result.appendChild(node)});

        function escapeTags(sql){
            const replacements = {};
                replacements["<"] = "&lt;";
                replacements[">"] = "&gt;";

            return bulkReplaceAll(sql, replacements);
        }

        function createNode(sql){
            const node = document.createElement("DIV");
                node.innerHTML = sql;
            return node;
        }

        function generateMobileMessageSql(messageType, os, version, showContact){
            const template = "INSERT INTO master_data.mobile_message_configuration(message_type, platform, value, show_contact)"
                 + " VALUES ('{messageType}', '{os}', '{version}', '{showContact}')"
                 + " ON CONFLICT (message_type, platform) DO UPDATE SET value='{version}', show_contact='{showContact}';";

            const replacements = {};
                replacements["{messageType}"] = messageType;
                replacements["{os}"] = os;
                replacements["{version}"] = version;
                replacements["{showContact}"] = showContact;

            return bulkReplaceAll(template, replacements);
        }

        function generateLocalizedTextSql(messageType, type, os, locale, value){
            const template = "INSERT INTO master_data.localized_text(key, locale, value)"
                + " VALUES('{messageType}_{type}_{os}', '{locale}', '{value}')"
                + " ON CONFLICT (key, locale) DO UPDATE SET value='{value}';";

            const replacements = {};
                replacements["{messageType}"] = messageType;
                replacements["{os}"] = os;
                replacements["{type}"] = type;
                replacements["{locale}"] = locale;
                replacements["{value}"] = value;

            return bulkReplaceAll(template, replacements);
        }
    }
})();