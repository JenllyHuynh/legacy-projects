import AppNavbar from "./components/AppNavbar";
import OrchidList from "./components/OrchidList";
import AppFooter from "./components/AppFooter";

function App() {
    return (
        <>
            <AppNavbar />

            <main>
                <OrchidList />
            </main>

            <AppFooter />
        </>
    );
}

export default App;