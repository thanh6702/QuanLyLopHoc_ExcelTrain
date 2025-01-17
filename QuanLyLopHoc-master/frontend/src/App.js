import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import ClassPage from './pages/ClassPage'; // Màn lớp học
import StudentPage from './pages/StudentPage'; // Màn học sinh
import TeacherPage from './pages/TeacherPage'; // Màn giáo viên

function App() {
    return (
        <Router>
            <div className="App">
                <Switch>
                    <Route path="/class" component={ClassPage} />
                    <Route path="/students" component={StudentPage} />
                    <Route path="/teachers" component={TeacherPage} />
                    {/* Các route khác có thể thêm vào đây */}
                </Switch>
            </div>
        </Router>
    );
}

export default App;
