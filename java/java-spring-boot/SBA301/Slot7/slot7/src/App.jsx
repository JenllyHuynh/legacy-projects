import { Routes, Route } from "react-router-dom";
import AppNavbar from "./components/AppNavbar.jsx";
import AppFooter from "./components/AppFooter.jsx";

import Home from "./pages/Home.jsx";
import EventsPage from "./pages/EventsPage.jsx";
import EventDetail from "./pages/EventDetail.jsx";
import About from "./pages/About.jsx";
import NotFound from "./pages/NotFound.jsx";

function App() {
    return (
        <>
            <AppNavbar />

            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/events" element={<EventsPage />} />
                <Route path="/events/:id" element={<EventDetail />} />
                <Route path="/about" element={<About />} />
                <Route path="*" element={<NotFound />} />
            </Routes>

            <AppFooter />
        </>
    );
}

export default App;