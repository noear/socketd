import {MessageInternal} from "./Message";

/**
 * 握手信息
 *
 * @author noear
 * @since 2.0
 */
export interface Handshake {
    /**
     * 协议版本
     */
    version(): string;

    /**
     * 获请传输地址
     *
     * @return tcp://192.168.0.1/path?user=1&path=2
     */
    uri(): URL;

    /**
     * 获取参数集合
     */
    paramMap(): URLSearchParams

    /**
     * 获取参数
     *
     * @param name 参数名
     */
    param(name: string): string;

    /**
     * 获取参数或默认值
     *
     * @param name 参数名
     * @param def  默认值
     */
    paramOrDefault(name: string, def: string): string;

    /**
     * 设置或修改参数
     */
    paramSet(name: string, value: string);
}


/**
 * @author noear
 * @since 2.0
 */
export interface HandshakeInternal extends Handshake {
    /**
     * 获取消息源
     */
    getSource(): MessageInternal;
}