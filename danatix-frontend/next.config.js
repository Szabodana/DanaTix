/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  env: {
    JWT_SECRET_KEY: process.env.JWT_SECRET_KEY
  }
}

module.exports = nextConfig