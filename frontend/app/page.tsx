import Link from "next/link";

export default function Home() {
  return (
    <div className="w-screen h-screen flex items-center justify-center gap-4">
      <Link href="/upload">
        <p className="px-6 py-4 bg-white text-black rounded-2xl">Upload</p>
      </Link>
      <Link href="/patients">
        <p className="px-6 py-4 bg-white text-black rounded-2xl">Patients</p>
      </Link>
    </div>
  );
}
