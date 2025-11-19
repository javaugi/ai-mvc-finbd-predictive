React Testing Library Examples for Frontend Testing
Here's a comprehensive guide to testing React components with React Testing Library (RTL), including different testing scenarios and best practices.

Basic Component Test
jsx
import { render, screen } from '@testing-library/react';
import Button from './Button';

test('renders button with correct text', () => {
  render(<Button>Click me</Button>);
  const buttonElement = screen.getByRole('button', { name: /click me/i });
  expect(buttonElement).toBeInTheDocument();
});
Form Testing Example
jsx
import { render, screen, fireEvent } from '@testing-library/react';
import LoginForm from './LoginForm';

test('submits form with email and password', async () => {
  const mockSubmit = jest.fn();
  render(<LoginForm onSubmit={mockSubmit} />);
  
  // Fill out the form
  fireEvent.change(screen.getByLabelText(/email/i), {
    target: { value: 'user@example.com' }
  });
  fireEvent.change(screen.getByLabelText(/password/i), {
    target: { value: 'password123' }
  });
  
  // Submit the form
  fireEvent.click(screen.getByRole('button', { name: /sign in/i }));
  
  // Assertions
  expect(mockSubmit).toHaveBeenCalledWith({
    email: 'user@example.com',
    password: 'password123'
  });
});
Async/Await Test Example
jsx
import { render, screen, waitFor } from '@testing-library/react';
import UserList from './UserList';

test('displays user data after loading', async () => {
  // Mock API response
  global.fetch = jest.fn(() =>
    Promise.resolve({
      json: () => Promise.resolve([{ id: 1, name: 'John Doe' }]),
    })
  );

  render(<UserList />);
  
  // Verify loading state
  expect(screen.getByText(/loading/i)).toBeInTheDocument();
  
  // Wait for data to load
  await waitFor(() => {
    expect(screen.getByText('John Doe')).toBeInTheDocument();
  });
  
  // Verify loading is gone
  expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
});
Testing User Interactions
jsx
import { render, screen, fireEvent } from '@testing-library/react';
import Counter from './Counter';

test('increments counter when button is clicked', () => {
  render(<Counter />);
  const button = screen.getByRole('button', { name: /increment/i });
  const count = screen.getByTestId('count-value');
  
  expect(count).toHaveTextContent('0');
  
  fireEvent.click(button);
  expect(count).toHaveTextContent('1');
  
  fireEvent.click(button);
  expect(count).toHaveTextContent('2');
});
Mocking Context
jsx
import { render, screen } from '@testing-library/react';
import { ThemeProvider } from '../context/ThemeContext';
import ThemedButton from './ThemedButton';

test('uses theme from context', () => {
  render(
    <ThemeProvider value="dark">
      <ThemedButton />
    </ThemeProvider>
  );
  
  const button = screen.getByRole('button');
  expect(button).toHaveClass('dark-theme');
});
Testing Accessibility
jsx
import { render, screen } from '@testing-library/react';
import { axe } from 'jest-axe';
import AccessibleForm from './AccessibleForm';

test('has no accessibility violations', async () => {
  const { container } = render(<AccessibleForm />);
  const results = await axe(container);
  expect(results).toHaveNoViolations();
});

test('has proper aria labels', () => {
  render(<AccessibleForm />);
  expect(screen.getByRole('textbox', { name: /username/i })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /submit/i })).toBeInTheDocument();
});
Best Practices
Query Priority: Prefer queries in this order:

getByRole

getByLabelText

getByPlaceholderText

getByText

getByDisplayValue

getByAltText

getByTitle

getByTestId

Async Utilities: Use findBy* queries for elements that appear asynchronously

User-Centric Tests: Test components the way users interact with them (not implementation details)

Mocking: Use jest.mock() for complex dependencies

Cleanup: No need to manually cleanup with React Testing Library v9+ (it does this automatically)

These examples cover the most common testing scenarios in React applications using React Testing Library. The library encourages testing components in a way that resembles how users interact with your application, leading to more maintainable and reliable tests.

This is my frontend React structure. src/
├── auth/               # Auth-related files
│   ├── AuthContext.js  # React context for auth state
│   ├── Login.js        # Login component
│   └── PrivateRoute.js # Protected routes
├── api/
│   └── axiosConfig.js  # Axios instance with interceptors
├── App.js
├── index.js   where should I add those test scripts and having the test scripts run automatically in the build process?
Here's how to structure your tests and integrate them into your build process:

Test File Structure
Add test files alongside your components with .test.js or .test.jsx extensions following this pattern:

text
src/
├── auth/
│   ├── AuthContext.js
│   ├── AuthContext.test.js  # Test file for context
│   ├── Login.js
│   ├── Login.test.js        # Test file for component
│   ├── PrivateRoute.js
│   └── PrivateRoute.test.js
├── api/
│   ├── axiosConfig.js
│   └── axiosConfig.test.js
├── App.js
├── App.test.js
├── index.js
└── setupTests.js           # Global test setup
Example Test Files
Login.test.js

jsx
import { render, screen, fireEvent } from '@testing-library/react';
import Login from './Login';
import { AuthProvider } from './AuthContext';

test('renders login form', () => {
  render(
    <AuthProvider>
      <Login />
    </AuthProvider>
  );
  
  expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
});

test('submits login form', async () => {
  const mockLogin = jest.fn();
  
  render(
    <AuthProvider value={{ login: mockLogin }}>
      <Login />
    </AuthProvider>
  );
  
  fireEvent.change(screen.getByLabelText(/username/i), {
    target: { value: 'testuser' }
  });
  fireEvent.change(screen.getByLabelText(/password/i), {
    target: { value: 'password123' }
  });
  fireEvent.click(screen.getByRole('button', { name: /login/i }));
  
  expect(mockLogin).toHaveBeenCalledWith({
    username: 'testuser',
    password: 'password123'
  });
});
PrivateRoute.test.js

jsx
import { render } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import PrivateRoute from './PrivateRoute';

test('redirects when not authenticated', () => {
  // Test redirection logic
});

test('renders component when authenticated', () => {
  // Test authenticated access
});
Setup Files
setupTests.js (at src level)

js
import '@testing-library/jest-dom';
import { TextEncoder, TextDecoder } from 'util';

// Polyfills for Jest environment
global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;
Integrating with Build Process
Install required packages (if not already installed):

bash
npm install --save-dev jest @testing-library/react @testing-library/jest-dom @testing-library/user-event jest-environment-jsdom
Update package.json scripts:

json
{
  "scripts": {
    "test": "react-scripts test",
    "test:watch": "react-scripts test --watch",
    "test:coverage": "react-scripts test --coverage",
    "build": "react-scripts build && npm run test:ci",
    "test:ci": "CI=true react-scripts test --watchAll=false --coverage"
  }
}
Create jest.config.js (if using Create React App, this is optional as CRA provides defaults):

js
module.exports = {
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['<rootDir>/src/setupTests.js'],
  moduleNameMapper: {
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
  },
  collectCoverageFrom: [
    'src/**/*.{js,jsx}',
    '!src/index.js',
    '!src/serviceWorker.js',
    '!src/setupTests.js'
  ]
};
CI/CD Integration
For automatic testing in your build pipeline:

GitHub Actions (.github/workflows/test.yml):

yaml
name: Test
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-node@v2
        with:
          node-version: '16'
      - run: npm install
      - run: npm run test:ci
      - run: npm run build
GitLab CI (.gitlab-ci.yml):

yaml
test:
  image: node:16
  script:
    - npm install
    - npm run test:ci
    - npm run build
Key Points
Test Location: Keep test files next to the components they test

Naming Convention: Use .test.js or .test.jsx suffix

Automatic Execution: Tests will run:

During npm run build (through the test:ci script)

In your CI/CD pipeline

When you run npm test locally

Coverage Reports: The --coverage flag generates reports in /coverage

This setup ensures your tests run automatically before builds and in your CI pipeline, helping catch issues early while maintaining a clean project structure.
