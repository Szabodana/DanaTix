import {NextResponse} from "next/server";
import {getToken} from "next-auth/jwt";

const middleware = async (req) => {
    const session = await getToken({
        req: req,
        secret: process.env.NEXT_AUTH_JWT_SECRET,
    });

    const whitelistedDynamicUrls = [
        '/_next',
        '/favicon',
        '/api',
        '/img',
    ]

    const whitelistedRoutesForUserAuthorization = [
        '/',
        '/articles',
        '/email-verification',
        '/login',
        '/register',
        ]

    const whitelistedRoutesForEmailVerification = [
        '/',
        '/articles',
        '/email-verification',
        '/login',
        '/register',
        '/unverified',
        '/profile',
        ]

    const url = req.nextUrl.pathname;

    if(whitelistedDynamicUrls.some((path) => url.startsWith(path))) {
        return NextResponse.next();
    }

    if(!whitelistedRoutesForUserAuthorization.includes(url) && !session) {
        return NextResponse.redirect(process.env.NEXT_PUBLIC_FRONTEND_URL + '/login');
    }

    if(!whitelistedRoutesForEmailVerification.includes(url) && !session.user.isEmailVerified) {
        return NextResponse.redirect(process.env.NEXT_PUBLIC_FRONTEND_URL + '/unverified');
    }

    return NextResponse.next();
}

export default middleware;