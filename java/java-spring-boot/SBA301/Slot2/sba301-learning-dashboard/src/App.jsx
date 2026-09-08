import ArchitectureFlow from "./components/ArchitectureFlow";
import CourseHeader from "./components/CourseHeader";
import EnvironmentStatus from "./components/EnvironmentStatus";
import Footer from "./components/Footer";
import LearningChecklist from "./components/LearningChecklist";
import ProjectSummary from "./components/ProjectSummary";
import StudentProfile from "./components/StudentProfile";

function App() {
  return (
    <>
      <CourseHeader />
      <main className="dashboard">
        <StudentProfile />
        <EnvironmentStatus />
        <LearningChecklist />
        <ProjectSummary />
        <ArchitectureFlow />
      </main>
      <Footer />
    </>
  );
}

export default App;
