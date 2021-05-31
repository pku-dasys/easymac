#include <bits/stdc++.h>
using namespace std;

const int NN=64;
string out_as;
int N;
const int M=6;
int f[M][NN+NN-1],h[M][NN+NN-1],initV[NN+NN-1];

int V[NN+NN-1];
// 1 3:2, 0 2:2
void output()
{
	cerr<<"orz";
	//cerr<<"orz\n";return ;exit(0);
	int i,j;
	cout<<N+N-1<<" "<<M<<endl;
	for(j=N+N-2;j>=0;j--)
	{
		V[j]=initV[j];
		;cout<<V[j]<<" ";
	}
	;cout<<endl;
	//FILE *my_out=fopen("output1","w");
	int as=0;
	for(i=0;i<M;i++)
	{
		for(j=N+N-2;j>=0;j--)
		{
			if(3*f[i][j]+2*h[i][j]>V[j]){cerr<<"smg!!";while(1);}
			V[j]-=2*f[i][j]+h[i][j];
			if(j)V[j]+=f[i][j-1]+h[i][j-1];
			as+=f[i][j]+h[i][j];
			cout<<f[i][j]<<" "<<h[i][j]<<"   ";
			//if(i==M-1)cout<<V[j]<<" ";
			//if(i==1)cout<<f[i][j]<<" ";
		}
		cout<<endl;
		//if(i==8)cout<<endl;
		//if(i==M-1)cout<<endl;
	}
	
	FILE *my_out=fopen(out_as.c_str(),"w");
	fprintf(my_out,"%d %d\n%d\n",N,N,as);
	for(i=0;i<M;i++)
		for(j=N+N-2;j>=0;j--)
		{
			for(int k=0;k<f[i][j];k++)fprintf(my_out,"%d 1\n",j);
			for(int k=0;k<h[i][j];k++)fprintf(my_out,"%d 0\n",j);
		}
	freopen("v2.txt","w",stdout);
	cout<<N+N-1<<endl;
	for(i=N+N-2;i>=0;i--)
	    cout<<V[i]<<" ";
	exit(0);
	;//cout<<endl;
	//exit(0);
}

void dfs(int nowlt,int t,int lt,int i,int V,int flg1,int flg2)//第t级，上一列最后一级为lt,第i列,有V个点 
{
	//cout<<t<<" "<<lt<<" "<<i<<" "<<V<<endl;
	if(i==N+N-1)
	{
		output();
		return ;
	}
	int nowflg1=0;
	if(t&&i)
	{
	    V+=f[t-1][i-1]+h[t-1][i-1];
	    if(f[t-1][i-1]+h[t-1][i-1])nowflg1=1;
	}
	
	if(V<=2&&t>lt)
	{
		if(flg1||!flg2)
    	    dfs(-1,0,nowlt,i+1,initV[i+1],0,0);
	    if(V<=1)return ;
	    else {flg1=0;flg2=1;}
	}
	if(V>=3&&i==N+N-2)return ;
	if(t==M)return ;
	
	f[t][i]=V/3;h[t][i]=0;
	dfs((V/3)?t:nowlt,t+1,lt,i,V-(2*f[t][i]+h[t][i]),(V/3)?1:flg1,flg2);
	f[t][i]=0;h[t][i]=0;
	
	if(V%3==2&&(V>2||nowflg1))
	{
	   f[t][i]=V/3;h[t][i]=1;
	   dfs(t,t+1,lt,i,V-(2*f[t][i]+h[t][i]),1,flg2);
	   f[t][i]=0;h[t][i]=0;
	}
}

void init()
{
	int i;
	for(i=0;i<N;i++)
    	initV[i]=i+1;
 	for(i=N;i<N+N-1;i++)
    	initV[i]=N+N-1-i;
}

int get_int(char* s)
{
	int i,as=0;
	for(i=0;i<strlen(s);i++)as=as*10+s[i]-'0';
	return as;
}

int main(int argc,char* argv[])
{
	N=get_int(argv[1]);
	out_as=argv[2];
	freopen("fh2.txt","w",stdout);
	init();
	dfs(-1,0,0,0,initV[0],0,0);
	return 0;
}

