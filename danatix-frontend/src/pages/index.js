import {Inter} from 'next/font/google'
import LandingPage from "@/pages/articles";

const inter = Inter({subsets: ['latin']})

export default function Home() {
    return (
            <LandingPage/>
    );
}