import NextAuth from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";
import GitHubProvider from "next-auth/providers/github";
import DiscordProvider from "next-auth/providers/discord";

const authOptions = {
    providers: [
        GitHubProvider({
            clientId: process.env.GITHUB_CLIENT_ID,
            clientSecret: process.env.GITHUB_CLIENT_SECRET,
        }),
        DiscordProvider({
            clientId: process.env.DISCORD_CLIENT_ID,
            clientSecret: process.env.DISCORD_CLIENT_SECRET,
        }),
        CredentialsProvider({
            name: "Credentials",
            credentials: {
                email: {label: "Email", type: "text", placeholder: "jsmith"},
                password: {label: "Password", type: "password"}
            },
            async authorize(credentials, req) {
                const {email, password} = credentials;
                const res = await fetch(process.env.NEXT_PUBLIC_API_URL + "/login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        email,
                        password,
                    }),
                });

                const response = await res.json();
                const token = response.token;

                if (res.ok && token && typeof token === 'string') {
                    const payloadBase64 = token.split('.')[1];
                    const payloadString = Buffer.from(payloadBase64, 'base64').toString('utf-8');
                    const payload = JSON.parse(payloadString);

                    return {
                        email: payload.email,
                        name: payload.name,
                        isEmailVerified: payload.isEmailVerified,
                        accessToken: token
                    };
                } else {
                    throw new Error("Credentials are incorrect");
                }
            },
        }),
    ],
    pages: {
        signIn: '/login',
        signOut: '/auth/signout',
        error: '/auth/error',
        verifyRequest: '/auth/verify-request',
        newUser: null
    },
    session: {
        strategy: "jwt",
    },
    callbacks: {
        async jwt({ token, user, account }) {
            if (user) {
                token.accessToken = account?.accessToken;
                token.user = user;
            }
            return token;
        },
        async session({ session, token }) {
            session.user = token.user;
            session.accessToken = token.accessToken;
            return session;
        }
    },
    jwt: {
        secret: process.env.NEXT_AUTH_JWT_SECRET,
    },
};

export default NextAuth(authOptions);