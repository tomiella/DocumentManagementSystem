export default function Login() {
    return (
        <div className="max-w-md mx-auto mt-20 bg-white border rounded p-6 space-y-4">
            <h2 className="text-xl font-semibold">Sign in</h2>
            <input className="border rounded px-3 py-2 w-full" placeholder="User Name" />
            <input className="border rounded px-3 py-2 w-full" placeholder="Password" type="password" />
            <button className="bg-black text-white rounded px-3 py-2 w-full">Sign In</button>
            <button className="border rounded px-3 py-2 w-full">Sign Up</button>
        </div>
    );
}
