package com.github.imdabigboss.dragonweb.client.responses;

public enum HTTPCodes {
    CODE_100("Continue", 100),
    CODE_101("Switching Protocol", 101),
    CODE_102("Processing", 102),
    CODE_103("Early Hints", 103),

    CODE_200("OK", 200),
    CODE_201("Created", 201),
    CODE_202("Accepted", 202),
    CODE_203("Non-Authoritative Information", 203),
    CODE_204("No Content", 204),
    CODE_205("Reset Content", 205),
    CODE_206("Partial Content", 206),
    CODE_207("Multi-Status", 207),
    CODE_208("Already Reported", 208),
    CODE_226("IM Used", 226),

    CODE_300("Multiple Choice", 300),
    CODE_301("Moved Permanently", 301),
    CODE_302("Found", 302),
    CODE_303("See Other", 303),
    CODE_304("Not Modified", 304),
    CODE_305("Use Proxy", 305),
    CODE_306("", 306), //Reserved, currently unused
    CODE_307("Temporary Redirect", 307),
    CODE_308("Permanent Redirect", 308),

    CODE_400("Bad Request", 400),
    CODE_401("Unauthorized", 401),
    CODE_402("Payment Required", 402),
    CODE_403("Forbidden", 403),
    CODE_404("Not Found", 404),
    CODE_405("Method Not Allowed", 405),
    CODE_406("Not Acceptable", 406),
    CODE_407("Proxy Authentication Required", 407),
    CODE_408("Request Timeout", 408),
    CODE_409("Conflict", 409),
    CODE_410("Gone", 410),
    CODE_411("Length Required", 411),
    CODE_412("Precondition Failed", 412),
    CODE_413("Payload Too Large", 413),
    CODE_414("URI Too Long", 414),
    CODE_415("Unsupported Media Type", 415),
    CODE_416("Range Not Satisfiable", 416),
    CODE_417("Expectation Failed", 417),
    CODE_418("I'm a teapot", 418),
    CODE_421("Misdirected Request", 421),
    CODE_422("Unprocessable Entity", 422),
    CODE_423("Locked", 423),
    CODE_424("Failed Dependency", 424),
    CODE_425("Too Early", 425),
    CODE_426("Upgrade Required", 426),
    CODE_428("Precondition Required", 428),
    CODE_429("Too Many Requests", 429),
    CODE_431("Request Header Fields Too Large", 431),
    CODE_451("Unavailable For Legal Reasons", 451),

    CODE_500("Internal Server Error", 500),
    CODE_501("Not Implemented", 501),
    CODE_502("Bad Gateway", 502),
    CODE_503("Service Unavailable", 503),
    CODE_504("Gateway Timeout", 504),
    CODE_505("HTTP Version Not Supported", 505),
    CODE_506("Variant Also Negotiates", 506),
    CODE_507("Insufficient Storage", 507),
    CODE_508("Loop Detected", 508),
    CODE_510("Not Extended", 510),
    CODE_511("Network Authentication Required", 511);

    private final String text;
    private final int code;

    HTTPCodes(final String text, final int code) {
        this.text = text;
        this.code = code;
    }

    @Override
    public String toString() {
        return text;
    }
    public int getCode() {
        return code;
    }
}
