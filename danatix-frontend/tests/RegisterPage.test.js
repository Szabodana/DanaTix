import React from 'react';
import {render, fireEvent, waitFor, screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import RegisterPage from '@/pages/register';
import fetchMock from 'jest-fetch-mock';
import {SessionProvider} from "next-auth/react";

fetchMock.enableMocks();

describe('RegisterPage', () => {
    it('renders without errors', () => {
        const {
            getByText,
            getByPlaceholderText
        } = render(<SessionProvider session={null}><RegisterPage/></SessionProvider>);

        expect(getByText('Registration')).toBeInTheDocument();
        expect(getByPlaceholderText('Username')).toBeInTheDocument();
        expect(getByPlaceholderText('Email')).toBeInTheDocument();
        expect(getByPlaceholderText('Password')).toBeInTheDocument();
    });
    it('handles form submission and validation', async () => {
        fetchMock.mockResponseOnce(JSON.stringify({success: true}));

        render(
            <SessionProvider session={null}>
                <RegisterPage/>
            </SessionProvider>
        );

        fireEvent.change(screen.getByPlaceholderText('Username'), {
            target: {value: 'testuser'},
        });
        fireEvent.change(screen.getByPlaceholderText('Email'), {
            target: {value: 'test@example.com'},
        });
        fireEvent.change(screen.getByPlaceholderText('Password'), {
            target: {value: 'password123'},
        });

        fireEvent.click(screen.getByLabelText('Please accept the Terms and Conditions'));

        fireEvent.submit(screen.getByTestId('register-button'));

        await waitFor(() => {
            expect(fetchMock).toHaveBeenCalledWith(
                process.env.NEXT_PUBLIC_API_URL + '/register',
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        name: 'testuser',
                        email: 'test@example.com',
                        password: 'password123',
                    }),
                }
            );
        });
    });
});