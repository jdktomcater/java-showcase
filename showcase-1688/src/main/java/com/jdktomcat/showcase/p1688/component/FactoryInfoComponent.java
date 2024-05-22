package com.jdktomcat.showcase.p1688.component;

import cn.hutool.core.net.url.UrlBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class FactoryInfoComponent {

    /**
     * 查询地址
     */
    public static final String QUERY_URL = "https://search.1688.com/service/companyInfoSearchDataService";

    public static final String COOKIE = "cna=x2q5HXvGODgCAT0JZ+3G/ERs; taklid=0709bacabf614b46925c79b1b16d3daa; inquiry_preference=webIM; mtop_partitioned_detect=1; _m_h5_tk=ea1786aa4b72a83a2c48807d4329af74_1716387720947; _m_h5_tk_enc=306ec525d0a512573a9e6a9b89aa9d19; xlly_s=1; cbu_mmid=7E3C55520E991B9B6A5D97E08A6697F6915E0D462D5722229BC2A5F0F015980209F848C1F03189F222CD52D098667A99BF69F74DD283188F8F602E4F69A50290; ta_info=431EEA0EF470984D28D52453CC0DA412B173CDCAB5755DCF50F0FE8F811E949D09EAF4563BC57F77236C0DC1F722D904169D41243043DB9AD8013D5559D7DDBB1DCAD8DF9E5BE921710532AFFAA956CAE9918CBA45AD426B12EAF9BB38DC72C32C4B272476917DCCC20C0218A8E1A53207A65255644812C1C8FB083FB71B1053F89523F2DAF8B0C269564CA354BE593083588926B45F8DD261FED64189CCA017CAC75904C23DE14353FDD96D35D97DEF900CF152CDC7FF431A552072B5751DFEEFA92895D1427C804D431779BEFC0DB4E2994D834A2C0B1ED73CB05EB1E76B783BB048D06DCE4245; cookie1=W80r5pRLWa9MNhlo6wPSYtowub65gUlIMTeSc05%2BzYA%3D; cookie2=1a1fd345ed47baba8d54645897af8209; cookie17=Uoe1gqlyvjVUmA%3D%3D; sgcookie=E100VV8p9Pmyzl%2FZvBOM4kP1Bty5Q023nVbuhUDVSCM%2BF%2Fxn3dpN8FGCeKh3ubizxi0m7IDWaouZfjaiqdy4FDXCSPHFpS%2BJ1HqsgtmRxbcXQviOOmWHASUEKLFgoqC%2BEWGO; t=b220292dc927b4211315cd7b657c9a37; _tb_token_=5beaa94ee14e3; sg=734; csg=860dc396; lid=tang_qi37; unb=1610101773; uc4=id4=0%40UO%2BzPdI1NXtcfuaElVmZ5gs%2FaVxw&nk4=0%40FY0dfvoBj8S5XnCUBAAH0%2Blqh1w%3D; __cn_logon__=true; __cn_logon_id__=tang_qi37; ali_apache_track=c_mid=b2b-1610101773|c_lid=tang_qi37|c_ms=1; ali_apache_tracktmp=c_w_signed=Y; _nk_=tang_qi37; last_mid=b2b-1610101773; _csrf_token=1716379202459; __mwb_logon_id__=tang_qi37; mwb=tm; keywordsHistory=%E9%87%91%E5%B1%9E%3B%E9%87%91%E5%B1%9E%E5%88%B6%E5%93%81%3B%E6%98%86%E5%B1%B1%E9%94%9F%E6%98%B6%E9%A1%BA%E9%87%91%E5%B1%9E%E5%88%B6%E5%93%81%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8; tfstk=fyhjHUbXOijjyq37j-LzNBO8VzV1cmOU6Nat-VCVWSFYXYaa8GItWnms1ung0-PwnAOT2Vh4DFv0fPn-7Z-00EV_BcnYBcrZuZDoY2CqgKVN1SVg6H-eTBuqo5VOJi9tXif8WPAN6lCvHslqzH-eTQk-sAukYAuDEpw7SuUA6-BvPuUQ55UTk5L72PahBinT6Ug8qPXODlEAyTU_RjwGfPTacqtZXxizO9vxlu1OOw4bHp3PQ1fMlr2Yv8E56zl7l-ZKkjASIrU-abwUUN-Qw2DrDzNB1_Fbdxnby4x1OoHE-RcTFQ14rxnjwRGySK0S1owKM8_1970uWcesEH1UlSk7djMDSg3xYoMLiV7B04wIFJ48eN6YgvuEsJhvGMqzKPiQRmsf4ofUALzNCawh1zZePU6GQnPE6rlQK3wQHz4bgUT5c7eYrzhx_U62Y-Uuzt8WPiNR.; isg=BOfn3eYnTbPk2sltiPT-on3kdhuxbLtOp3lC3rlXM3adqARqwT5en5ruzqg2QJPG";

    /**
     * 组装查询参数
     *
     * @param keywords 关键字
     * @return 查询参数
     */
    private String makeupQueryParam(String keywords) {
        UrlBuilder urlBuilder = UrlBuilder.ofHttp(QUERY_URL);
        urlBuilder.addQuery("keywords",keywords);
        urlBuilder.addQuery("spm", "a26352.13672862.searchbox.input");
        urlBuilder.addQuery("sortType", "wwResponseRate");
        urlBuilder.addQuery("n", "y");
        urlBuilder.addQuery("async", true);
        urlBuilder.addQuery("asyncCount", 6);
        urlBuilder.addQuery("beginPage", 1);
        urlBuilder.addQuery("pageSize", 20);
        urlBuilder.addQuery("requestId", "KddhEy3bMKeQ42CM4jnrNpZC4jahrfzFrwy1716380049199");
        urlBuilder.addQuery("startIndex", 0);
        urlBuilder.addQuery("pageName", "findPCFactory");
        return urlBuilder.build();
    }

    /**
     * 组装头参数
     *
     * @return 查询头参数
     */
    private Request.Builder makeupHeaderParam(Request.Builder builder) {
        return builder.addHeader("accept", "*/*")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .addHeader("cookie", "cna=x2q5HXvGODgCAT0JZ+3G/ERs; taklid=0709bacabf614b46925c79b1b16d3daa; inquiry_preference=webIM; mtop_partitioned_detect=1; _m_h5_tk=ea1786aa4b72a83a2c48807d4329af74_1716387720947; _m_h5_tk_enc=306ec525d0a512573a9e6a9b89aa9d19; xlly_s=1; cbu_mmid=7E3C55520E991B9B6A5D97E08A6697F6915E0D462D5722229BC2A5F0F015980209F848C1F03189F222CD52D098667A99BF69F74DD283188F8F602E4F69A50290; ta_info=431EEA0EF470984D28D52453CC0DA412B173CDCAB5755DCF50F0FE8F811E949D09EAF4563BC57F77236C0DC1F722D904169D41243043DB9AD8013D5559D7DDBB1DCAD8DF9E5BE921710532AFFAA956CAE9918CBA45AD426B12EAF9BB38DC72C32C4B272476917DCCC20C0218A8E1A53207A65255644812C1C8FB083FB71B1053F89523F2DAF8B0C269564CA354BE593083588926B45F8DD261FED64189CCA017CAC75904C23DE14353FDD96D35D97DEF900CF152CDC7FF431A552072B5751DFEEFA92895D1427C804D431779BEFC0DB4E2994D834A2C0B1ED73CB05EB1E76B783BB048D06DCE4245; cookie1=W80r5pRLWa9MNhlo6wPSYtowub65gUlIMTeSc05%2BzYA%3D; cookie2=1a1fd345ed47baba8d54645897af8209; cookie17=Uoe1gqlyvjVUmA%3D%3D; sgcookie=E100VV8p9Pmyzl%2FZvBOM4kP1Bty5Q023nVbuhUDVSCM%2BF%2Fxn3dpN8FGCeKh3ubizxi0m7IDWaouZfjaiqdy4FDXCSPHFpS%2BJ1HqsgtmRxbcXQviOOmWHASUEKLFgoqC%2BEWGO; t=b220292dc927b4211315cd7b657c9a37; _tb_token_=5beaa94ee14e3; sg=734; csg=860dc396; lid=tang_qi37; unb=1610101773; uc4=id4=0%40UO%2BzPdI1NXtcfuaElVmZ5gs%2FaVxw&nk4=0%40FY0dfvoBj8S5XnCUBAAH0%2Blqh1w%3D; __cn_logon__=true; __cn_logon_id__=tang_qi37; ali_apache_track=c_mid=b2b-1610101773|c_lid=tang_qi37|c_ms=1; ali_apache_tracktmp=c_w_signed=Y; _nk_=tang_qi37; last_mid=b2b-1610101773; _csrf_token=1716379202459; __mwb_logon_id__=tang_qi37; mwb=tm; keywordsHistory=%E9%87%91%E5%B1%9E%3B%E9%87%91%E5%B1%9E%E5%88%B6%E5%93%81%3B%E6%98%86%E5%B1%B1%E9%94%9F%E6%98%B6%E9%A1%BA%E9%87%91%E5%B1%9E%E5%88%B6%E5%93%81%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8; tfstk=fyhjHUbXOijjyq37j-LzNBO8VzV1cmOU6Nat-VCVWSFYXYaa8GItWnms1ung0-PwnAOT2Vh4DFv0fPn-7Z-00EV_BcnYBcrZuZDoY2CqgKVN1SVg6H-eTBuqo5VOJi9tXif8WPAN6lCvHslqzH-eTQk-sAukYAuDEpw7SuUA6-BvPuUQ55UTk5L72PahBinT6Ug8qPXODlEAyTU_RjwGfPTacqtZXxizO9vxlu1OOw4bHp3PQ1fMlr2Yv8E56zl7l-ZKkjASIrU-abwUUN-Qw2DrDzNB1_Fbdxnby4x1OoHE-RcTFQ14rxnjwRGySK0S1owKM8_1970uWcesEH1UlSk7djMDSg3xYoMLiV7B04wIFJ48eN6YgvuEsJhvGMqzKPiQRmsf4ofUALzNCawh1zZePU6GQnPE6rlQK3wQHz4bgUT5c7eYrzhx_U62Y-Uuzt8WPiNR.; isg=BOfn3eYnTbPk2sltiPT-on3kdhuxbLtOp3lC3rlXM3adqARqwT5en5ruzqg2QJPG")
                .addHeader("origin", "https://s.1688.com")
                .addHeader("priority", "u=1, i")
                .addHeader("referer", "https://s.1688.com/")
                .addHeader("sec-ch-ua", "\"Microsoft Edge\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "same-site")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36 Edg/125.0.0.0");
    }

    public String exportOk(String keywords) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://search.1688.com/service/companyInfoSearchDataService?keywords=%BD%F0%CA%F4&spm=a26352.13672862.searchbox.input&sortType=wwResponseRate&n=y&async=true&asyncCount=6&beginPage=1&pageSize=20&requestId=KddhEy3bMKeQ42CM4jnrNpZC4jahrfzFrwy1716380049199&sessionId=b05821faabaf4f848356da869b5b46c6&startIndex=0&pageName=findPCFactory")
                .method("GET", null)
                .addHeader("accept", "*/*")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .addHeader("cookie", "cna=x2q5HXvGODgCAT0JZ+3G/ERs; taklid=0709bacabf614b46925c79b1b16d3daa; inquiry_preference=webIM; mtop_partitioned_detect=1; _m_h5_tk=ea1786aa4b72a83a2c48807d4329af74_1716387720947; _m_h5_tk_enc=306ec525d0a512573a9e6a9b89aa9d19; xlly_s=1; cbu_mmid=7E3C55520E991B9B6A5D97E08A6697F6915E0D462D5722229BC2A5F0F015980209F848C1F03189F222CD52D098667A99BF69F74DD283188F8F602E4F69A50290; ta_info=431EEA0EF470984D28D52453CC0DA412B173CDCAB5755DCF50F0FE8F811E949D09EAF4563BC57F77236C0DC1F722D904169D41243043DB9AD8013D5559D7DDBB1DCAD8DF9E5BE921710532AFFAA956CAE9918CBA45AD426B12EAF9BB38DC72C32C4B272476917DCCC20C0218A8E1A53207A65255644812C1C8FB083FB71B1053F89523F2DAF8B0C269564CA354BE593083588926B45F8DD261FED64189CCA017CAC75904C23DE14353FDD96D35D97DEF900CF152CDC7FF431A552072B5751DFEEFA92895D1427C804D431779BEFC0DB4E2994D834A2C0B1ED73CB05EB1E76B783BB048D06DCE4245; cookie1=W80r5pRLWa9MNhlo6wPSYtowub65gUlIMTeSc05%2BzYA%3D; cookie2=1a1fd345ed47baba8d54645897af8209; cookie17=Uoe1gqlyvjVUmA%3D%3D; sgcookie=E100VV8p9Pmyzl%2FZvBOM4kP1Bty5Q023nVbuhUDVSCM%2BF%2Fxn3dpN8FGCeKh3ubizxi0m7IDWaouZfjaiqdy4FDXCSPHFpS%2BJ1HqsgtmRxbcXQviOOmWHASUEKLFgoqC%2BEWGO; t=b220292dc927b4211315cd7b657c9a37; _tb_token_=5beaa94ee14e3; sg=734; csg=860dc396; lid=tang_qi37; unb=1610101773; uc4=id4=0%40UO%2BzPdI1NXtcfuaElVmZ5gs%2FaVxw&nk4=0%40FY0dfvoBj8S5XnCUBAAH0%2Blqh1w%3D; __cn_logon__=true; __cn_logon_id__=tang_qi37; ali_apache_track=c_mid=b2b-1610101773|c_lid=tang_qi37|c_ms=1; ali_apache_tracktmp=c_w_signed=Y; _nk_=tang_qi37; last_mid=b2b-1610101773; _csrf_token=1716379202459; __mwb_logon_id__=tang_qi37; mwb=tm; keywordsHistory=%E9%87%91%E5%B1%9E%3B%E9%87%91%E5%B1%9E%E5%88%B6%E5%93%81%3B%E6%98%86%E5%B1%B1%E9%94%9F%E6%98%B6%E9%A1%BA%E9%87%91%E5%B1%9E%E5%88%B6%E5%93%81%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8; tfstk=fyhjHUbXOijjyq37j-LzNBO8VzV1cmOU6Nat-VCVWSFYXYaa8GItWnms1ung0-PwnAOT2Vh4DFv0fPn-7Z-00EV_BcnYBcrZuZDoY2CqgKVN1SVg6H-eTBuqo5VOJi9tXif8WPAN6lCvHslqzH-eTQk-sAukYAuDEpw7SuUA6-BvPuUQ55UTk5L72PahBinT6Ug8qPXODlEAyTU_RjwGfPTacqtZXxizO9vxlu1OOw4bHp3PQ1fMlr2Yv8E56zl7l-ZKkjASIrU-abwUUN-Qw2DrDzNB1_Fbdxnby4x1OoHE-RcTFQ14rxnjwRGySK0S1owKM8_1970uWcesEH1UlSk7djMDSg3xYoMLiV7B04wIFJ48eN6YgvuEsJhvGMqzKPiQRmsf4ofUALzNCawh1zZePU6GQnPE6rlQK3wQHz4bgUT5c7eYrzhx_U62Y-Uuzt8WPiNR.; isg=BOfn3eYnTbPk2sltiPT-on3kdhuxbLtOp3lC3rlXM3adqARqwT5en5ruzqg2QJPG")
                .addHeader("origin", "https://s.1688.com")
                .addHeader("priority", "u=1, i")
                .addHeader("referer", "https://s.1688.com/")
                .addHeader("sec-ch-ua", "\"Microsoft Edge\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "same-site")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36 Edg/125.0.0.0")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    /**
     * 查询工厂相关信息
     *
     * @param keywords 关键字信息
     * @return 结果
     */
    public String export(String keywords) {
        try {
            String body = exportOk(keywords);
            log.info("resp: {}", body + keywords);
            return body;
        } catch (Exception ex) {
            log.error("异常！", ex);
            return ex.getMessage();
        }
    }
}
