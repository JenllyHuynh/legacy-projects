import { Container } from "react-bootstrap";

function About() {
    return (
        <Container className="py-5">
            <h1>About Campus Event Navigator</h1>

            <p>
                Campus Event Navigator is a React single-page application
                for exploring and navigating campus events.
            </p>

            <p>
                This project demonstrates React Router, reusable components,
                dynamic routes, URL parameters and browser navigation.
            </p>
        </Container>
    );
}

export default About;