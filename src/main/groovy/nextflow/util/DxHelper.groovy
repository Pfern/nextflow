package nextflow.util

import com.dnanexus.DXAPI
import com.dnanexus.DXJSON
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import groovy.util.logging.Slf4j
import org.apache.http.HttpVersion
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.CoreProtocolPNames

/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */

@Slf4j
class DxHelper {

    static void downloadFile( String fileId, File targetFile ) {

        def download = DXAPI.fileDownload(fileId)
        def url = download.get('url').textValue()
        def headers = download.get('headers')

        log.debug "download headers>>>\n" + headers.toString()

        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        def get = new HttpGet(url)
        for( Map.Entry<String,JsonNode> item : headers.fields() ) {
            log.debug "setting header > ${item.key}: ${item.value.textValue()}"
            get.setHeader( item.key, item.value.textValue())
        }

        def commandOutFile = targetFile
        BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(commandOutFile))
        try {
            client.execute(get).getEntity().writeTo(buffer)
        }
        finally {
            buffer.close()
        }

    }


    /**
     * Converts a {@code Map} to a {@code ObjectNode} instance
     *
     * @param params
     * @return
     */
    static ObjectNode mapToJsonNode( Map params ) {
        assert params != null

        def node = DXJSON.getObjectBuilder()
        params.each { String name, value ->

            switch( value ) {
                case boolean:
                    node = node.put(name, value.asBoolean() );
                    break

                case Integer:
                    node = node.put(name, value as Integer)
                    break

                case Long:
                    node = node.put(name, value as Long)
                    break

                case double:
                    node = node.put(name, value as Double)
                    break

                case Map:
                    node = node.put(name, mapToJsonNode(value as Map))
                    break

                default:
                    node = node.put(name, value?.toString() )

            }

        }

        node.build()
    }


//    static jsonNodeToMap( JsonNode node ) {
//
//        def result = [:]
//        jsonNodeToMap(node, result)
//        return result
//    }

//    static jsonNodeToMap( JsonNode node, Map result  ) {
//
//
//        if( node.isArray() ) {
//            def list = []
//            for( def item : node.elements() ) {
//                list << jsonNodeToMap(item)
//            }
//
//        }
//        else if( node.isBigDecimal() ) {
//
//        }
//        else if( node.isBigInteger() ) {
//
//        }
//        else if( node.isBinary() ) {
//
//        }
//        else if( node.isBoolean() ) {
//
//        }
//        else if( node.isDouble() ) {
//
//        }
//        else if( node.isFloatingPointNumber()) {
//
//        }
//        else if( node.isInt() ) {
//
//        }
//        else if( node.isLong()) {
//
//        }
//        else if( node.isNumber()) {
//
//        }
//        else if( node.isObject()) {
//
//        }
//        else if( node.isPojo() ) {
//
//        }
//        else if( node.isTextual()) {
//
//        }
//        else if( node.isValueNode()) {
//
//        }
//
//        else if( node.isNull()) {
//
//        }
//        else {
//
//        }
//
//
//
//    }

}
