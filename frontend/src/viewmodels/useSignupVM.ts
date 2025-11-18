import {useState} from "react";
import {paperless} from "../api/paperless";
import type {UserDTO} from "../models/UserDTO";

export function useSignupVM() {

    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);


    // const [username, setUsername] = useState('');
    // const [email, setEmail] = useState('');
    // const [password, setPassword] = useState('');
    // const [busy, setBusy] = useState(false);
    // const [error, setError] = useState<string | null>(null);
    // const [result, setResult] = useState<UserDTO | null>(null);


    async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        setError(null);
        setSubmitting(true);
        try {
            const fd = new FormData(e.currentTarget);
            const username = String(fd.get('username') || '').trim();
            const email = String(fd.get('email') || '').trim();
            const password = String(fd.get('password') || '').trim();
            const confirm = String(fd.get('confirm') || '').trim();
            if (!username) return setError('Username is required.');
            if (!email) { setError("Please enter an email"); return}
            if (!password) { setError("Please enter a password"); return}
            if (password.length < 6) { setError("Password must be at least 6 characters"); return}
        }catch (e) {
            if (e instanceof Error) {
                setError(e.message);
            } else {
                setError('An unknown error occurred.');
            }
        } finally {setSubmitting(false);
        }
    }
}